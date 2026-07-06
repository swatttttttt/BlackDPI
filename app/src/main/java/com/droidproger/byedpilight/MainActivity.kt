package com.droidproger.byedpilight

import android.content.pm.PackageManager.FEATURE_LEANBACK
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.droidproger.byedpilight.ui.MainScreen
import com.droidproger.byedpilight.ui.SavedCmdScreen
import com.droidproger.byedpilight.ui.SettingsScreen
import com.droidproger.byedpilight.ui.TvScreen
import com.droidproger.byedpilight.ui.theme.ComposeAppTheme
import com.droidproger.byedpilight.ui.theme.TvComposeTheme
import com.droidproger.byedpilight.utility.registerReceiver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val dataModel = DataModel()

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefStore = PrefStore(applicationContext)//
        lifecycleScope.launch {
            dataModel.cmdLine = prefStore.cmdLine.first()
            dataModel.proxyPort = prefStore.proxyPort.first()
            dataModel.dnsIp = prefStore.dnsIp.first()
            dataModel.provider = prefStore.provider.first()
            dataModel.sniHost = prefStore.sniHost.first()
            dataModel.udpOverTcp = prefStore.udpOverTcp.first()
            dataModel.obfuscationEnabled = prefStore.obfuscation.first()
            dataModel.ipv6enabled = prefStore.ipv6Enable.first()
            dataModel.mobile = prefStore.autoStartMobile.first()
            dataModel.anyConn = prefStore.autoStartOther.first()
            if (dataModel.mobile or dataModel.anyConn){
                if (!dataModel.receiverRegistered){
                    registerReceiver(applicationContext)
                }
            }
        }
        enableEdgeToEdge()
        setContent {
            if (packageManager.hasSystemFeature(FEATURE_LEANBACK)){
                TvComposeTheme {
                        CreateTvUi(
                            prefStore
                        )
                }
            }else{
                ComposeAppTheme {
                        dataModel.textMinLines = 5
                        CreateUi(
                            prefStore
                        )
                }
            }
        }
    }

}

@Composable
fun CreateUi(prefStore: PrefStore) {//,activity: Activity
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main"){
        composable("main"){
            MainScreen(navController)//,activity
        }
        composable("settings"){
            SettingsScreen(navController, prefStore)
        }
        composable("savedcmd"){
            SavedCmdScreen(navController, prefStore)
        }
    }
}

@Composable
fun CreateTvUi(prefStore: PrefStore){
    TvScreen(prefStore)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeAppTheme {
        CreateUi(prefStore = PrefStore(LocalContext.current))
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DarkPreview() {
    ComposeAppTheme {
        CreateUi(prefStore = PrefStore(LocalContext.current))
    }
}