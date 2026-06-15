
package com.droidproger.byedpilight.utility

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.VpnService
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.droidproger.byedpilight.R
import com.droidproger.byedpilight.data.ServiceStatus
import com.droidproger.byedpilight.dataModel
import com.droidproger.byedpilight.services.ServiceManager


class NetworkReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null ) {

            if (dataModel.stoppedManually){ // do not work until reconnect or restart
                return
            }
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                if (dataModel.mobile){
                    startDpi(context)
                }else if (!dataModel.startedManually){
                    stopDpi(context)
                }
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) or
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) or
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                if (dataModel.anyConn){
                    startDpi(context)
                }else if (!dataModel.startedManually){
                    stopDpi(context)
                }
            }
        }else{
            stopDpi(context)
        }
    }
}

fun startDpi(context: Context){
    if (dataModel.serviceStatus != ServiceStatus.Connected){
        val intentPrepare = VpnService.prepare(context)
        if (intentPrepare != null) {
            Toast.makeText(context, R.string.vpnPermissionDenied, Toast.LENGTH_SHORT).show()
        } else {
            ServiceManager.start(context)
        }
        dataModel.startedManually = false
    }
}

fun stopDpi(context: Context){
    if (dataModel.serviceStatus == ServiceStatus.Connected){
        ServiceManager.stop(context)
    }else{
        dataModel.stoppedManually = false //reset to default
    }
}

fun registerReceiver(context: Context){
    val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
    ContextCompat.registerReceiver(
        context,//applicationContext,
        dataModel.receiver,
        filter,
        ContextCompat.RECEIVER_NOT_EXPORTED
    )
    dataModel.receiverRegistered = true
    //this.registerReceiver(dataModel.receiver,filter)
}

fun checkReceiver(context: Context){
    if (!dataModel.mobile && !dataModel.anyConn){
        try {
            context.unregisterReceiver(dataModel.receiver)
        }catch (e:IllegalArgumentException){

        }
    }
    dataModel.receiverRegistered = false
}