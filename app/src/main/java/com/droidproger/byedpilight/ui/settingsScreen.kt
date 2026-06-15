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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
    Column(
        Modifier
            .wrapContentSize()
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
fun ByeDpiSettings(prefStore: PrefStore, scope: CoroutineScope){//,navController: NavController
    //val context = LocalContext.current
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
        SetButtons(prefStore, 1, scope)
    }
    val text = remember { mutableStateOf(tempCmdLine) }//
    TextField(
        value = text.value,
        onValueChange = {
            tempCmdLine = it
            text.value = it//tempCmdLine
        },
        Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        enabled = dataModel.isCmdEdit,
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

@Composable
fun Tun2socksSettings(prefStore: PrefStore, scope: CoroutineScope){

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.hevsocks5Port),
            Modifier
                //.weight(0.1f)
                .padding(10.dp, 0.dp, 10.dp, 0.dp)
        )
        val text = remember { mutableStateOf(tempProxyPort.toString()) }//
        TextField(
            value = text.value,
            onValueChange = {
                text.value = it
                if (it.length > 0) {
                    tempProxyPort = it.toInt()
                }
            },
            Modifier
                .weight(0.9f)
                .padding(8.dp),
            placeholder = { Text(stringResource(R.string.hevsocks5PortHint)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = dataModel.isPortEdit,
        )
        SetButtons(prefStore, 2, scope)
    }
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.hevsocks5Dns),
            Modifier
                //.weight(0.1f)
                .padding(10.dp, 0.dp, 10.dp, 0.dp)
        )
        val text = remember { mutableStateOf(tempDns) }//
        TextField(
            value = text.value,
            onValueChange = {
                tempDns = it
                text.value = it
            },
            Modifier
                .weight(0.9f)
                .padding(8.dp),
            enabled = dataModel.isDnsEdit,
        )
        SetButtons(prefStore, 3, scope)
    }
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
                scope.launch {
                    prefStore.saveIpv6enable(it)
                }
            },
            Modifier
                .padding(10.dp, 10.dp, 10.dp, 10.dp)
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

@Composable
fun SetButtons(prefStore: PrefStore, id: Int, scope: CoroutineScope){
    var isEdit = false
    when (id){
        1 -> isEdit = dataModel.isCmdEdit
        2 -> isEdit = dataModel.isPortEdit
        3 -> isEdit = dataModel.isDnsEdit
    }
    if (isEdit) {//dataModel.isCmdEdit
        IconButton( // close button
            onClick = {
                when (id){
                    1 -> closeCmdEdit()
                    2 -> closePortEdit()
                    3 -> closeDnsEdit()
                }
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null
            )
        }
        IconButton( // save button
            onClick = {
                when (id){
                    1 -> saveCmd(prefStore, scope)
                    2 -> savePort(prefStore,scope)
                    3 -> saveDns(prefStore,scope)
                }
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = null
            )
        }

    }else{
        IconButton( // edit button
            onClick = {
                when (id){
                    1 -> editCmd()
                    2 -> editPort()
                    3 -> editDns()
                }
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = null
            )
        }
    }
}

fun closeCmdEdit(){
    dataModel.isCmdEdit = false
    tempCmdLine = dataModel.cmdLine
}

fun saveCmd(prefStore: PrefStore, scope: CoroutineScope){
    dataModel.isCmdEdit = false
    dataModel.cmdLine = tempCmdLine
    scope.launch {
        prefStore.saveCmdLine(tempCmdLine)
    }
}

fun editCmd(){
    if (dataModel.serviceStatus == ServiceStatus.Connected){
        // show warning
        dataModel.showWarn = true
    }else{
        dataModel.isCmdEdit = true
    }
}

fun saveCmdToJson(){
    dataModel.saveToJson = true
}
fun closePortEdit(){
    dataModel.isPortEdit = false
    tempProxyPort = dataModel.proxyPort
}

fun savePort(prefStore: PrefStore, scope: CoroutineScope){
    dataModel.isPortEdit = false
    dataModel.proxyPort = tempProxyPort
    scope.launch {
        prefStore.saveProxyPort(tempProxyPort)
    }
}
fun editPort(){
    if (dataModel.serviceStatus == ServiceStatus.Connected){
        // show warning
        dataModel.showWarn = true
    }else{
        dataModel.isPortEdit = true
    }
}
fun closeDnsEdit(){
    dataModel.isDnsEdit = false
    tempDns = dataModel.dnsIp
}

fun saveDns(prefStore: PrefStore, scope: CoroutineScope){
    dataModel.isDnsEdit = false
    dataModel.dnsIp = tempDns
    scope.launch {
        prefStore.saveDns(tempDns)
    }
}
fun editDns(){
    if (dataModel.serviceStatus == ServiceStatus.Connected){
        // show warning
        dataModel.showWarn = true
    }else{
        dataModel.isDnsEdit = true
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
