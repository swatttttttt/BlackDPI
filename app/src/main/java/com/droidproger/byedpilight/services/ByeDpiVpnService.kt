package com.droidproger.byedpilight.services

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.droidproger.byedpilight.MainActivity
import com.droidproger.byedpilight.R
import com.droidproger.byedpilight.core.ByeDpiProxy
import com.droidproger.byedpilight.core.TProxyService
import com.droidproger.byedpilight.data.START_ACTION
import com.droidproger.byedpilight.data.STOP_ACTION
import com.droidproger.byedpilight.data.ServiceStatus
import com.droidproger.byedpilight.dataModel
import com.droidproger.byedpilight.utility.MSTOP
import com.droidproger.byedpilight.utility.createConnectionNotification
import com.droidproger.byedpilight.utility.registerNotificationChannel
import com.droidproger.byedpilight.utility.shellSplit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

class ByeDpiVpnService : LifecycleVpnService() {
    private val byeDpiProxy = ByeDpiProxy()
    private var proxyJob: Job? = null
    private var tunFd: ParcelFileDescriptor? = null
    private val mutex = Mutex()
    private var stopping: Boolean = false

    companion object {
        private val TAG: String = ByeDpiVpnService::class.java.simpleName
        private const val FOREGROUND_SERVICE_ID: Int = 1
        private const val NOTIFICATION_CHANNEL_ID: String = "ByeDPIVpn"

        private var status: ServiceStatus = ServiceStatus.Disconnected
    }

    override fun onCreate() {
        super.onCreate()
        registerNotificationChannel(
            this,
            NOTIFICATION_CHANNEL_ID,
            R.string.vpn_channel_name,
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return when (val action = intent?.action) {
            START_ACTION -> {
                lifecycleScope.launch { start() }
                START_STICKY
            }
            STOP_ACTION -> {
                if (intent.hasExtra(MSTOP)) { //
                    dataModel.stoppedManually = intent.getBooleanExtra(MSTOP,false)
                }
                lifecycleScope.launch { stop() }
                START_NOT_STICKY
            }

            else -> {
                Log.w(TAG, "Unknown action: $action")
                START_NOT_STICKY
            }
        }
    }

    override fun onRevoke() {
        Log.i(TAG, "VPN revoked")
        lifecycleScope.launch { stop() }
    }

    private suspend fun start() {
        Log.i(TAG, "Starting")

        if (status == ServiceStatus.Connected) {
            Log.w(TAG, "VPN already connected")
            return
        }

        try {
            mutex.withLock {
                startProxy()
                startTun2Socks()
            }
            updateStatus(ServiceStatus.Connected)
            startForeground()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start VPN", e)
            updateStatus(ServiceStatus.Failed)
            stop()
        }
    }

    private fun startForeground() {
        val notification: Notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                FOREGROUND_SERVICE_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            startForeground(FOREGROUND_SERVICE_ID, notification)
        }
    }

    private suspend fun stop() {
        Log.i(TAG, "Stopping")

        mutex.withLock {
            stopping = true
            try {
                stopTun2Socks()
                stopProxy()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stop VPN", e)
            } finally {
                stopping = false
            }
        }

        updateStatus(ServiceStatus.Disconnected)
        stopSelf()
    }

    private suspend fun startProxy() {
        Log.i(TAG, "Starting proxy")

        if (proxyJob != null) {
            Log.w(TAG, "Proxy fields not null")
            throw IllegalStateException("Proxy fields not null")
        }

        val preferences = cmdToArgs(dataModel.cmdLine)//getByeDpiPreferences()

        proxyJob = lifecycleScope.launch(Dispatchers.IO) {
            val code = byeDpiProxy.startProxy(preferences)

            withContext(Dispatchers.Main) {
                if (code != 0) {
                    Log.e(TAG, "Proxy stopped with code $code")
                    updateStatus(ServiceStatus.Failed)
                } else {
                    if (!stopping) {
                        stop()
                        updateStatus(ServiceStatus.Disconnected)
                    }
                }
            }
        }

        Log.i(TAG, "Proxy started")
    }

    private suspend fun stopProxy() {
        Log.i(TAG, "Stopping proxy")

        if (status == ServiceStatus.Disconnected) {
            Log.w(TAG, "Proxy already disconnected")
            return
        }

        byeDpiProxy.stopProxy()
        proxyJob?.join() ?: throw IllegalStateException("ProxyJob field null")
        proxyJob = null

        Log.i(TAG, "Proxy stopped")
    }

    private fun startTun2Socks() {
        Log.i(TAG, "Starting tun2socks")

        if (tunFd != null) {
            throw IllegalStateException("VPN field not null")
        }
        val port = dataModel.proxyPort
        val dns = dataModel.dnsIp
        val ipv6 = dataModel.ipv6enabled

        val udpOverTcp = dataModel.udpOverTcp
        // When UDP-over-TCP is on: tunnel UDP inside TCP connections at the
        // tun2socks level (udp: tcp), reduce MTU to 1400 (TCP-friendly payload
        // size for wrapped UDP datagrams), and double the coroutine stack so
        // the extra per-flow TCP state fits without overflow.
        val tun2socksConfig = """
        | misc:
        |   task-stack-size: ${if (udpOverTcp) 163840 else 81920}
        | socks5:
        |   mtu: ${if (udpOverTcp) 1400 else 8500}
        |   address: 127.0.0.1
        |   port: $port
        |   udp: ${if (udpOverTcp) "tcp" else "udp"}
        """.trimMargin("| ")

        val configPath = try {
            File.createTempFile("config", "tmp", cacheDir).apply {
                writeText(tun2socksConfig)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create config file", e)
            throw e
        }

        val fd = createBuilder(dns, ipv6).establish()
            ?: throw IllegalStateException("VPN connection failed")

        this.tunFd = fd

        TProxyService.TProxyStartService(configPath.absolutePath, fd.fd)

        Log.i(TAG, "Tun2Socks started")
    }

    private fun stopTun2Socks() {
        Log.i(TAG, "Stopping tun2socks")

        TProxyService.TProxyStopService()

        try {
            File(cacheDir, "config.tmp").delete()
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to delete config file", e)
        }

        tunFd?.close() ?: Log.w(TAG, "VPN not running")
        tunFd = null

        Log.i(TAG, "Tun2socks stopped")
    }

    private fun updateStatus(newStatus: ServiceStatus) {
        Log.d(TAG, "VPN status changed from $status to $newStatus")

        status = newStatus
        dataModel.serviceStatus = newStatus
        if (newStatus == ServiceStatus.Disconnected || newStatus == ServiceStatus.Failed){
            proxyJob = null
        }
    }

    private fun createNotification(): Notification =
        createConnectionNotification(
            this,
            NOTIFICATION_CHANNEL_ID,
            R.string.notification_title,
            R.string.vpn_notification_content,
            ByeDpiVpnService::class.java,
        )

    private fun createBuilder(dns: String, ipv6: Boolean): Builder {
        Log.d(TAG, "DNS: $dns")
        val builder = Builder()
        builder.setSession("ByeDPI")
        builder.setConfigureIntent(
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE,
            )
        )

        builder.addAddress("10.10.10.10", 32)
            .addRoute("0.0.0.0", 0)

        if (ipv6) {
            builder.addAddress("fd00::1", 128)
                .addRoute("::", 0)
        }

        if (dns.isNotBlank()) {
            builder.addDnsServer(dns)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            builder.setMetered(false)
        }

        builder.addDisallowedApplication(applicationContext.packageName)

        return builder
    }

    private fun cmdToArgs(cmd: String): Array<String> {
        val firstArgIndex = cmd.indexOf("-")
        val argsStr = (if (firstArgIndex > 0) cmd.substring(firstArgIndex) else cmd).trim()
        val baseArgs = shellSplit(argsStr)
        val provider = dataModel.provider.takeIf { it.isNotBlank() } ?: "auto"
        val strategyArgs = mutableListOf<String>()

        if (provider != "auto") {
            strategyArgs += "-s"
            strategyArgs += provider
        }
        if (dataModel.sniHost.isNotBlank()) {
            strategyArgs += "-n"
            strategyArgs += dataModel.sniHost
        }
        if (dataModel.udpOverTcp) {
            // -U: tell ciadpi to disable direct UDP forwarding so all UDP
            // traffic is handled by the TCP tunnel set up in tun2socks config.
            strategyArgs += "-U"
        }

        if (dataModel.obfuscationEnabled) {
            // ── GROUP 0 ── primary multi-layer desync (AmneziaWG-style) ──────
            //
            // Apply to all protocol types: TLS/HTTPS (t), HTTP (h), UDP (u).
            strategyArgs += "-K"; strategyArgs += "tuh"

            // TLS record fragmentation 1 byte after the SNI field starts.
            // The DPI reassembler sees two incomplete TLS records whose SNI
            // bytes are split across a segment boundary — unclassifiable.
            strategyArgs += "-r"; strategyArgs += "1+s"

            // DESYNC_FAKE at byte 1: inject a decoy packet with a very low TTL
            // before the real ClientHello.  Like AmneziaWG junk packets — DPI
            // builds state for the fake, then the real data arrives and doesn't
            // match, leaving the DPI state machine in an undefined state.
            strategyArgs += "-f"; strategyArgs += "1"

            // TTL = 4: fake packet expires inside the ISP backbone (typically
            // 2–3 hops to their DPI cluster) and never reaches the remote host.
            strategyArgs += "-t"; strategyArgs += "4"

            // Randomise fake packet content on every connection so it cannot
            // be fingerprinted or blocked by content signature.
            strategyArgs += "-Q"; strategyArgs += "r"

            // DESYNC_DISORDER at byte 3: send the tail segment before the head.
            // Standard DPI waits for in-order data; disorder breaks reassembly
            // on most hardware middleboxes without affecting the endpoint (TCP
            // reorders at the receiver side).
            strategyArgs += "-d"; strategyArgs += "3"

            // DESYNC_OOB at byte 4: inject a 1-byte urgent/OOB segment.
            // Many ISP DPI implementations either crash-handle or skip the
            // payload entirely when urgent data appears mid-stream.
            strategyArgs += "-o"; strategyArgs += "4"

            // HTTP obfuscation for plain-HTTP connections:
            //   r = extra space before Host (breaks Host-header parsers)
            //   h = randomise header-name casing  (Accept-Encoding → aCcEpT-)
            //   d = randomise domain case in Host value
            strategyArgs += "-M"; strategyArgs += "r,h,d"

            // Drop TCP SACK from SYN and data packets.  SACK lets DPI
            // track exactly which byte ranges arrived; without it the
            // reassembler loses the ability to close gaps after disorder.
            strategyArgs += "-Y"

            // Widen the desync window: hold the first data segment so the
            // fake/OOB packets enter the DPI pipeline first, corrupting its
            // connection state before the real payload is classified.
            strategyArgs += "-Z"

            // ── GROUP 1 ── auto-fallback if group 0 gets blocked ─────────────
            // Activate when DPI responds with TCP RST (t) or causes a TLS
            // handshake error (s) — i.e., the ISP's DPI actively terminated
            // the connection.  Uses different offsets and harder TTL.
            strategyArgs += "-A"; strategyArgs += "t,s"

            strategyArgs += "-K"; strategyArgs += "tuh"

            // Split at a different SNI position (2 bytes in) so the pattern
            // looks different from group 0 — avoids learning-DPI adaptation.
            strategyArgs += "-r"; strategyArgs += "2+s"

            // Fake at byte 2 with TTL=2: dies within 2 hops — guaranteed to
            // expire inside the ISP PoP, cannot reach any remote inspection.
            strategyArgs += "-f"; strategyArgs += "2"
            strategyArgs += "-t"; strategyArgs += "2"
            strategyArgs += "-Q"; strategyArgs += "r"

            // Shift disorder and OOB to different offsets so traffic patterns
            // differ from group 0 and confuse adaptive DPI signatures.
            strategyArgs += "-d"; strategyArgs += "5"
            strategyArgs += "-o"; strategyArgs += "3"

            strategyArgs += "-Y"
        }

        return arrayOf("ciadpi") + baseArgs + strategyArgs
    }

}
