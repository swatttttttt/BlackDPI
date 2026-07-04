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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.udpOverTcp),
            Modifier
                .weight(1f)
                .padding(10.dp, 10.dp, 10.dp, 10.dp)
        )
        var udpValue by remember { mutableStateOf(tempUdpOverTcp) }
        Switch(
            checked = udpValue,
            onCheckedChange = {
                tempUdpOverTcp = it
                udpValue = it
                dataModel.udpOverTcp = it
                scope.launch { prefStore.saveUdpOverTcp(it) }
            },
            modifier = Modifier.padding(10.dp, 10.dp, 10.dp, 10.dp)
        )
    }

    // --- IPv6 ---
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.ipv6enable),
            Modifier
                .weight(1f)
                .padding(10.dp, 10.dp, 10.dp, 10.dp)
        )
        Switch(
            checked = dataModel.ipv6enabled,
            onCheckedChange = {
                dataModel.ipv6enabled = it
                scope.launch { prefStore.saveIpv6enable(it) }
            },
            Modifier.padding(10.dp, 10.dp, 10.dp, 10.dp)
        )
    }
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
