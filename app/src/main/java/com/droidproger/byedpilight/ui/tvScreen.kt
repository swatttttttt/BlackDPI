package com.droidproger.byedpilight.ui

import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.droidproger.byedpilight.PrefStore
import com.droidproger.byedpilight.R
import com.droidproger.byedpilight.dataModel
import com.droidproger.byedpilight.ui.theme.TvComposeTheme
import com.droidproger.byedpilight.utility.collectLogs
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvScreen(prefStore: PrefStore){
    val TAG = "DpiApp"
    var menuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val logsRegister =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val logs = collectLogs()
            if (logs == null) {
                Toast.makeText(context,R.string.logsFailed,Toast.LENGTH_SHORT).show()
            } else {
                val uri = it.data?.data ?: run {
                    Log.e("DpiApp", "No data in result")
                    return@rememberLauncherForActivityResult //@launch
                }
                context.contentResolver.openOutputStream(uri)?.use {
                    try {
                        it.write(logs.toByteArray())
                    } catch (e: IOException) {
                        Log.e(TAG, "Failed to save logs", e)
                    }
                } ?: run {
                    Log.e(TAG, "Failed to open output stream")
                }
            }
        }
    Surface {
        Column (
            Modifier
                .fillMaxSize()
        ){
            TopAppBar(
                title = {
                    Text(stringResource(R.string.app_name) )
                },
                actions = {
                    IconButton(
                        onClick = {
                            menuExpanded = true
                        }
                    )
                    {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "menu"
                        )
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.saveLogs)) },
                                onClick = {
                                    val intent =
                                        Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                                            addCategory(Intent.CATEGORY_OPENABLE)
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TITLE, "byedpi.log")
                                        }

                                    logsRegister.launch(intent)
                                    menuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.about)) },
                                onClick = {
                                    menuExpanded = false
                                    dataModel.showAbout = true
                                }
                            )
                        }
                    }
                }

            )
            Column (
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Spacer(modifier = Modifier.height(30.dp))
                MainButtons(context)
                Spacer(modifier = Modifier.height(30.dp))
                val colorScheme = MaterialTheme.colorScheme
                val scope = rememberCoroutineScope()
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Column (
                        Modifier
                            .border(width = 2.dp, color = colorScheme.primary, shape= RoundedCornerShape(10.dp))
                            .padding(10.dp)
                            .wrapContentSize()
                            .weight(0.7f,false)
                            .verticalScroll(rememberScrollState())
                    ){
                        Text(
                            text = stringResource(R.string.byedpiSettings),
                            Modifier
                                .padding(start = 10.dp, top = 10.dp, end = 10.dp)
                                .fillMaxWidth()//.wrapContentSize()
                            ,
                            color = colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        ByeDpiSettings(prefStore,scope)
                        RequestPerm()
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column (
                        Modifier
                            .border(width = 2.dp, color = colorScheme.primary, shape= RoundedCornerShape(10.dp))
                            .padding(10.dp)
                            .wrapContentSize()
                            .weight(0.3f,false)
                            .verticalScroll(rememberScrollState())
                    ){
                        Text(
                            text = stringResource(R.string.hevsocks5Settings),
                            Modifier
                                .padding(start = 10.dp, top = 10.dp, end = 10.dp)
                                .fillMaxWidth(),//.wrapContentSize(),
                            color = colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Tun2socksSettings(prefStore,scope)
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
    if (dataModel.showAbout){
        AboutScreen()
    }
}

@Preview(showBackground = true, device = "spec:width=1920dp,height=1080dp,dpi=320")
@Composable
fun TvPreview() {
    TvComposeTheme {
        TvScreen(prefStore = PrefStore(LocalContext.current))
    }
}

@Preview(showBackground = true, device = "spec:width=1920dp,height=1080dp,dpi=320", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DarkTvPreview() {
    TvComposeTheme {
        TvScreen(prefStore = PrefStore(LocalContext.current))
    }
}