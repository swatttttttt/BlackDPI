package com.droidproger.byedpilight.ui


import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.droidproger.byedpilight.PrefStore
import com.droidproger.byedpilight.R
import com.droidproger.byedpilight.data.ServiceStatus
import com.droidproger.byedpilight.dataModel
import com.droidproger.byedpilight.ui.theme.ComposeAppTheme
import com.droidproger.byedpilight.utility.checkReceiver
import com.droidproger.byedpilight.utility.registerReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

var tempCmdLine = dataModel.cmdLine
var tempProxyPort = dataModel.proxyPort
var tempDns = dataModel.dnsIp
var tempProvider = dataModel.provider
var tempSniHost = dataModel.sniHost
var tempUdpOverTcp = dataModel.udpOverTcp
var tempObfuscation = dataModel.obfuscationEnabled

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, prefStore: PrefStore){
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Surface {
        Column (
            Modifier
                .fillMaxSize()
                .padding(bottom = bottomPadding)
        ) {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.settings))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            dataModel.isCmdEdit = false
                            navController.navigateUp()
                        }
                    )
                    {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                }

            )
            Settings(prefStore,navController)
            if (dataModel.showWarn) {
                StopServiceDialog()
            }else{
                if (dataModel.saveToJson){
                    SaveCmdToJsonDialog()
                }
            }
        }
    }
}

@Composable
fun Settings(prefStore: PrefStore, navController: NavController){
    val scope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme
    val isInitialized = remember { mutableStateOf(false) }
    if (!isInitialized.value) {
        tempCmdLine = dataModel.cmdLine
        tempProxyPort = dataModel.proxyPort
        tempDns = dataModel.dnsIp
        tempProvider = dataModel.provider
        tempSniHost = dataModel.sniHost
        tempUdpOverTcp = dataModel.udpOverTcp
        tempObfuscation = dataModel.obfuscationEnabled
        isInitialized.value = true
    }
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            Alignment.CenterVertically
        ) {
            Canvas(
                Modifier
                    //.size(width = 100.dp, height = 20.dp)
                    .weight(0.2f)
                    .padding(start = 10.dp)
            ) {
                val height = size.height
                val width = size.width
                drawLine(
                    start = Offset(x = 0f, y = height / 2),
                    end = Offset(x = width, y = height / 2),
                    color = colorScheme.primary,
                    strokeWidth = 6f,
                    alpha = 0.5f
                )
            }
            Text(
                text = stringResource(R.string.byedpiSettings),
                Modifier
                    .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)
                    .weight(0.8f)
                    .wrapContentSize(),
                color = colorScheme.primary, textAlign = TextAlign.Center
            )
            Canvas(
                Modifier
                    //.size(width = 100.dp, height = 20.dp)
                    .weight(0.2f)
                    .padding(end = 10.dp)
            ) {
                val height = size.height
                val width = size.width
                drawLine(
                    start = Offset(x = 0f, y = height / 2),
                    end = Offset(x = width, y = height / 2),
                    color = colorScheme.primary,
                    strokeWidth = 6f,
                    alpha = 0.5f
                )
            }
        }
        ByeDpiSettings(prefStore,scope)//,navController
        ManageCmd(navController)
        RequestPerm()
        // ----------------
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            Alignment.CenterVertically
        ) {
            Canvas(
                Modifier
                    //.size(width = 100.dp, height = 20.dp)
                    .weight(0.2f)
                    .padding(start = 10.dp)
            ) {
                val height = size.height
                val width = size.width
                drawLine(
                    start = Offset(x = 0f, y = height / 2),
                    end = Offset(x = width, y = height / 2),
                    color = colorScheme.primary,
                    strokeWidth = 6f,
                    alpha = 0.5f
                )
            }
            Text(
                text = stringResource(R.string.hevsocks5Settings),
                Modifier
                    .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)
                    .weight(0.8f)
                    .wrapContentSize(),
                color = colorScheme.primary, textAlign = TextAlign.Center
            )
            Canvas(
                Modifier
                    //.size(width = 100.dp, height = 20.dp)
                    .weight(0.2f)
                    .padding(end = 10.dp)
            ) {
                val height = size.height
                val width = size.width
                drawLine(
                    start = Offset(x = 0f, y = height / 2),
                    end = Offset(x = width, y = height / 2),
                    color = colorScheme.primary,
                    strokeWidth = 6f,
                    alpha = 0.5f
                )
            }
        }
        Tun2socksSettings(prefStore,scope)
        // --------------
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            Alignment.CenterVertically
        ) {
            Canvas(
                Modifier
                    //.size(width = 100.dp, height = 20.dp)
                    .weight(0.2f)
                    .padding(start = 10.dp)
            ) {
                val height = size.height
                val width = size.width
                drawLine(
                    start = Offset(x = 0f, y = height / 2),
                    end = Offset(x = width, y = height / 2),
                    color = colorScheme.primary,
                    strokeWidth = 6f,
                    alpha = 0.5f
                )
            }
            Text(
                text = stringResource(R.string.startDpiSettings),
                Modifier
                    .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)
                    .weight(0.8f)
                    .wrapContentSize(),
                color = colorScheme.primary, textAlign = TextAlign.Center
            )
            Canvas(
                Modifier
                    //.size(width = 100.dp, height = 20.dp)
                    .weight(0.2f)
                    .padding(end = 10.dp)
            ) {
                val height = size.height
                val width = size.width
                drawLine(
                    start = Offset(x = 0f, y = height / 2),
                    end = Offset(x = width, y = height / 2),
                    color = colorScheme.primary,
                    strokeWidth = 6f,
                    alpha = 0.5f
                )
            }
        }
        AutoStartSettings(prefStore,scope)
    }
}

@Composable
fun ByeDpiSettings(prefStore: PrefStore, scope: CoroutineScope){
    val isEnabled = dataModel.serviceStatus != ServiceStatus.Connected
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.putCMD),
            Modifier
                .weight(1f)
                .padding(10.dp, 0.dp, 0.dp, 0.dp)
        )
        IconButton(
            onClick = {
                dataModel.cmdLine = tempCmdLine
                scope.launch { prefStore.saveCmdLine(tempCmdLine) }
            },
            enabled = isEnabled
        ) {
            Icon(imageVector = Icons.Filled.Done, contentDescription = null)
        }
    }
    val text = remember { mutableStateOf(tempCmdLine) }
    TextField(
        value = text.value,
        onValueChange = {
            tempCmdLine = it
            text.value = it
        },
        Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        enabled = isEnabled,
        minLines = dataModel.textMinLines
    )
}
@Composable
fun ManageCmd(navController: NavController){
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        Alignment.CenterVertically
    ){
        ElevatedButton(
            onClick = {
                if (dataModel.serviceStatus == ServiceStatus.Connected){
                    // show warning
                    dataModel.showWarn = true
                }else{
                    navController.navigate("savedcmd")
                }
            },
            Modifier
                .weight(1f)
                .padding(start = 10.dp, end = 5.dp)
        ) {
            Text(
                text = stringResource(R.string.load)
            )
        }
        ElevatedButton(
            onClick = {
                saveCmdToJson()
            },
            Modifier
                .weight(1f)
                .padding(start = 5.dp, end = 10.dp)
        ) {
            Text(
                text = stringResource(R.string.save)
            )
        }
    }
}

@Composable
fun RequestPerm(){
    val context = LocalContext.current
    val permissionLauncher= rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode != RESULT_OK) {// granted
            Toast.makeText(context, R.string.accessPermDenied, Toast.LENGTH_SHORT).show()
        }
    }
    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (!granted) {
            Toast.makeText(context, R.string.accessPermDenied, Toast.LENGTH_SHORT).show()
        }
    }
    Text(
        stringResource(R.string.requestText),
        Modifier//.weight(1f)
            .padding(10.dp, 10.dp, 10.dp, 10.dp)
    )
    ElevatedButton(
        onClick = {
            val storageManager: Boolean
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                storageManager = Environment.isExternalStorageManager()
            } else {
                storageManager = (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED)
            }
            if (storageManager) {
                Toast.makeText(context, R.string.accessPermGranted, Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.data = Uri.fromParts("package", context.packageName, null)
                    try{
                        permissionLauncher.launch(intent)//
                    }catch (e: Exception){
                        val intent_ = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        try{
                            permissionLauncher.launch(intent_)//
                        }catch (e: Exception){
                            val intent_ = Intent(Settings.ACTION_APPLICATION_SETTINGS)
                            intent.data = Uri.fromParts("package", context.packageName, null)
                            try{
                                permissionLauncher.launch(intent_)//
                            }catch (e: Exception){

                                Toast.makeText(context, context.getString(R.string.accessPermError) +" - "+ e.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }else{
                    try{
                        requestPermissionsLauncher.launch(
                            arrayOf(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        )
                    }catch (e: Exception){
                        Toast.makeText(context, context.getString(R.string.accessPermError) +" - "+ e.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        },
        Modifier
            .fillMaxWidth()
            .padding(10.dp, 0.dp, 10.dp, 0.dp)
    ) {
        Text(
            stringResource(R.string.requestBtnText)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tun2socksSettings(prefStore: PrefStore, scope: CoroutineScope){
    val isEnabled = dataModel.serviceStatus != ServiceStatus.Connected

    // --- Proxy port ---
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.hevsocks5Port),
            Modifier.padding(10.dp, 0.dp, 10.dp, 0.dp)
        )
        val portText = remember { mutableStateOf(tempProxyPort.toString()) }
        TextField(
            value = portText.value,
            onValueChange = {
                portText.value = it
                val port = it.toIntOrNull()
                if (port != null) {
                    tempProxyPort = port
                    dataModel.proxyPort = port
                    scope.launch { prefStore.saveProxyPort(port) }
                }
            },
            Modifier
                .weight(0.9f)
                .padding(8.dp),
            placeholder = { Text(stringResource(R.string.hevsocks5PortHint)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = isEnabled,
        )
    }

    // --- DNS ---
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.hevsocks5Dns),
            Modifier.padding(10.dp, 0.dp, 10.dp, 0.dp)
        )
        val dnsOptions = listOf("77.88.8.8", "8.8.8.8", "1.1.1.1")
        var expanded by remember { mutableStateOf(false) }
        var dnsValue by remember { mutableStateOf(tempDns.ifBlank { dnsOptions.first() }) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (isEnabled) expanded = !expanded },
            modifier = Modifier
                .weight(0.9f)
                .padding(8.dp)
        ) {
            TextField(
                value = dnsValue,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                enabled = isEnabled,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                dnsOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            tempDns = option
                            dnsValue = option
                            dataModel.dnsIp = option
                            scope.launch { prefStore.saveDns(option) }
                            expanded = false
                        }
                    )
                }
            }
        }
    }

    // --- Provider strategy ---
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.providerStrategy),
            Modifier
                .weight(1f)
                .padding(10.dp, 10.dp, 10.dp, 10.dp)
        )
        val providerOptions = listOf("auto", "google", "yandex", "mailru", "beeline", "mts", "megafon", "tele2", "other")
        var providerExpanded by remember { mutableStateOf(false) }
        var providerValue by remember { mutableStateOf(tempProvider) }
        ExposedDropdownMenuBox(
            expanded = providerExpanded,
            onExpandedChange = { if (isEnabled) providerExpanded = !providerExpanded },
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            TextField(
                value = providerValue,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                enabled = isEnabled,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = providerExpanded) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                )
            )
            ExposedDropdownMenu(
                expanded = providerExpanded,
                onDismissRequest = { providerExpanded = false }
            ) {
                providerOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            tempProvider = option
                            providerValue = option
                            dataModel.provider = option
                            scope.launch { prefStore.saveProvider(option) }
                            providerExpanded = false
                        }
                    )
                }
            }
        }
    }

    // --- SNI host ---
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.sniHost),
            Modifier
                .weight(1f)
                .padding(10.dp, 10.dp, 10.dp, 10.dp)
        )
        val sniText = remember { mutableStateOf(tempSniHost) }
        TextField(
            value = sniText.value,
            onValueChange = {
                tempSniHost = it
                sniText.value = it
                dataModel.sniHost = it
                scope.launch { prefStore.saveSniHost(it) }
            },
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            enabled = isEnabled,
            placeholder = { Text(stringResource(R.string.sniHostHint)) }
        )
    }

    // --- UDP over TCP ---
    var udpValue by remember { mutableStateOf(tempUdpOverTcp) }
    UdpOverTcpCard(
        checked = udpValue,
        onToggle = {
            tempUdpOverTcp = it
            udpValue = it
            dataModel.udpOverTcp = it
            scope.launch { prefStore.saveUdpOverTcp(it) }
        }
    )

    // --- Traffic Obfuscation ---
    var obfValue by remember { mutableStateOf(tempObfuscation) }
    ObfuscationCard(
        checked = obfValue,
        onToggle = {
            tempObfuscation = it
            obfValue = it
            dataModel.obfuscationEnabled = it
            scope.launch { prefStore.saveObfuscation(it) }
        }
    )

    // --- IPv6 ---
    var ipv6Value by remember { mutableStateOf(dataModel.ipv6enabled) }
    Ipv6Card(
        checked = ipv6Value,
        onToggle = {
            ipv6Value = it
            dataModel.ipv6enabled = it
            scope.launch { prefStore.saveIpv6enable(it) }
        }
    )
}

@Composable
fun AutoStartSettings(prefStore: PrefStore, scope: CoroutineScope){
    val context = LocalContext.current
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.startDpiOnMobile),
            Modifier
                .weight(1f)
                .padding(10.dp, 10.dp, 10.dp, 10.dp)
        )
        Switch(
            checked = dataModel.mobile,
            onCheckedChange = {
                dataModel.mobile = it
                scope.launch {
                    prefStore.saveAutoStartMobile(it)
                }
                if (dataModel.mobile){
                    if (!dataModel.receiverRegistered){
                        registerReceiver(context.applicationContext)
                    }
                }else{
                    checkReceiver(context.applicationContext)
                }
            },
            Modifier
                .padding(10.dp, 10.dp, 10.dp, 10.dp)
        )
    }
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.startDpiOnAnyConn),
            Modifier
                .weight(1f)
                .padding(10.dp, 10.dp, 10.dp, 10.dp)
        )
        Switch(
            checked = dataModel.anyConn,
            onCheckedChange = {
                dataModel.anyConn = it
                scope.launch {
                    prefStore.saveAutoStartOther(it)
                }
                if (dataModel.anyConn){
                    if (!dataModel.receiverRegistered){
                        registerReceiver(context.applicationContext)
                    }
                }else{
                    checkReceiver(context.applicationContext)
                }
            },
            Modifier
                .padding(10.dp, 10.dp, 10.dp, 10.dp)
        )
    }

}

fun saveCmdToJson(){
    dataModel.saveToJson = true
}

@Composable
fun UdpOverTcpCard(checked: Boolean, onToggle: (Boolean) -> Unit) {
    val primary = MaterialTheme.colorScheme.primary

    val containerColor by animateColorAsState(
        targetValue = if (checked) primary.copy(alpha = 0.13f)
                      else MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "udpContainer"
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) primary.copy(alpha = 0.55f) else Color.Transparent,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "udpBorder"
    )
    val iconTint by animateColorAsState(
        targetValue = if (checked) primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "udpIcon"
    )

    // Pulse ring that animates only when active
    val infiniteTransition = rememberInfiniteTransition(label = "udpPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.55f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1400, easing = LinearEasing), RepeatMode.Restart),
        label = "pulseAlpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onToggle(!checked) },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Animated signal icon
            Canvas(modifier = Modifier.size(46.dp)) {
                val cx = size.width / 2f
                val cy = size.height * 0.62f
                val maxR = size.width * 0.42f
                val stroke = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round)

                // Pulse ring
                if (checked) {
                    drawCircle(
                        color = iconTint,
                        radius = maxR * pulseScale * 1.15f,
                        center = Offset(cx, cy),
                        alpha = pulseAlpha,
                        style = Stroke(width = 1.8.dp.toPx())
                    )
                }

                // 3 signal arcs (wifi-style, pointing up)
                val arcs = listOf(0.28f to 0.45f, 0.55f to 0.65f, 0.82f to 0.85f)
                arcs.forEachIndexed { idx, (radiusFactor, alpha) ->
                    val r = maxR * radiusFactor * 2
                    drawArc(
                        color = iconTint,
                        startAngle = 210f,
                        sweepAngle = 120f,
                        useCenter = false,
                        topLeft = Offset(cx - r / 2f, cy - r / 2f),
                        size = Size(r, r),
                        style = stroke,
                        alpha = if (checked) alpha else alpha * 0.5f
                    )
                }

                // Center dot
                drawCircle(
                    color = iconTint,
                    radius = 3.2.dp.toPx(),
                    center = Offset(cx, cy + maxR * 0.82f * 0.28f)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "UDP over TCP",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = if (checked)
                        "UDP-трафик туннелируется через TCP"
                    else
                        "Прямая передача UDP-пакетов",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
                if (checked) {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .background(primary.copy(alpha = 0.18f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "АКТИВНО",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = primary
                        )
                    }
                }
            }

            Switch(checked = checked, onCheckedChange = onToggle)
        }
    }
}

@Composable
fun ObfuscationCard(checked: Boolean, onToggle: (Boolean) -> Unit) {
    val secondary = MaterialTheme.colorScheme.secondary

    val containerColor by animateColorAsState(
        targetValue = if (checked) secondary.copy(alpha = 0.13f)
                      else MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "obfContainer"
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) secondary.copy(alpha = 0.55f) else Color.Transparent,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "obfBorder"
    )
    val iconTint by animateColorAsState(
        targetValue = if (checked) secondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "obfIcon"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onToggle(!checked) },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Shield icon drawn with Canvas
            Canvas(modifier = Modifier.size(46.dp)) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val w = size.width * 0.52f
                val h = size.height * 0.60f
                val top = cy - h / 2f
                val strokeW = 2.4.dp.toPx()

                // Shield outline: two rounded top corners + pointed bottom
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(cx, top + h)                          // bottom tip
                    lineTo(cx - w / 2f, cy + h * 0.10f)         // lower-left
                    quadraticTo(cx - w / 2f, top, cx, top + h * 0.08f) // upper-left arc
                    quadraticTo(cx + w / 2f, top, cx + w / 2f, cy + h * 0.10f) // upper-right arc
                    close()
                }
                drawPath(path, color = iconTint,
                    style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

                // Lock shackle (U-shape inside shield)
                val lw = w * 0.34f
                val lh = h * 0.22f
                val lTop = cy - h * 0.05f
                drawArc(
                    color = iconTint,
                    startAngle = 180f, sweepAngle = 180f,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(cx - lw / 2f, lTop - lh),
                    size = androidx.compose.ui.geometry.Size(lw, lh * 2f),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
                // Lock body rect
                val bw = lw * 1.5f; val bh = h * 0.26f
                drawRoundRect(
                    color = iconTint,
                    topLeft = androidx.compose.ui.geometry.Offset(cx - bw / 2f, cy + h * 0.04f),
                    size = androidx.compose.ui.geometry.Size(bw, bh),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()),
                    style = Stroke(width = strokeW)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Обфускация трафика",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = if (checked)
                        "Фейк-пакеты · disorder · OOB · TLS-сплит · 2 группы"
                    else
                        "Провайдер видит стандартный трафик",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
                if (checked) {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .background(secondary.copy(alpha = 0.18f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "АКТИВНО",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = secondary
                        )
                    }
                }
            }

            Switch(checked = checked, onCheckedChange = onToggle)
        }
    }
}

@Composable
fun Ipv6Card(checked: Boolean, onToggle: (Boolean) -> Unit) {
    val tertiary = MaterialTheme.colorScheme.tertiary

    val containerColor by animateColorAsState(
        targetValue = if (checked) tertiary.copy(alpha = 0.13f)
                      else MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "ipv6Container"
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) tertiary.copy(alpha = 0.55f) else Color.Transparent,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "ipv6Border"
    )
    val iconTint by animateColorAsState(
        targetValue = if (checked) tertiary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "ipv6Icon"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onToggle(!checked) },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Globe / network icon: circle + latitude + longitude arcs
            Canvas(modifier = Modifier.size(46.dp)) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val r = size.width * 0.38f
                val strokeW = 2.4.dp.toPx()
                val st = Stroke(width = strokeW, cap = StrokeCap.Round)

                // Outer circle
                drawCircle(color = iconTint, radius = r, center = androidx.compose.ui.geometry.Offset(cx, cy), style = st)
                // Horizontal equator
                drawLine(color = iconTint, start = androidx.compose.ui.geometry.Offset(cx - r, cy),
                    end = androidx.compose.ui.geometry.Offset(cx + r, cy), strokeWidth = strokeW)
                // Vertical axis
                drawLine(color = iconTint, start = androidx.compose.ui.geometry.Offset(cx, cy - r),
                    end = androidx.compose.ui.geometry.Offset(cx, cy + r), strokeWidth = strokeW)
                // Two longitude ellipses (drawn as arcs)
                val ew = r * 0.62f
                drawArc(color = iconTint, startAngle = 0f, sweepAngle = 180f, useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(cx - ew / 2f, cy - r),
                    size = androidx.compose.ui.geometry.Size(ew, r * 2f), style = st)
                drawArc(color = iconTint, startAngle = 180f, sweepAngle = 180f, useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(cx - ew / 2f, cy - r),
                    size = androidx.compose.ui.geometry.Size(ew, r * 2f), style = st)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "IPv6",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = if (checked)
                        "Двойной стек IPv4 + IPv6"
                    else
                        "Только IPv4",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
                if (checked) {
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .background(tertiary.copy(alpha = 0.18f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "ВКЛЮЧЕНО",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = tertiary
                        )
                    }
                }
            }

            Switch(checked = checked, onCheckedChange = onToggle)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    ComposeAppTheme {
        SettingsScreen(navController = rememberNavController(), prefStore = PrefStore(LocalContext.current))
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DarkSettingsPreview() {
    ComposeAppTheme {
        SettingsScreen(navController = rememberNavController(), prefStore = PrefStore(LocalContext.current))
    }
}
