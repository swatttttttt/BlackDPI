package com.droidproger.byedpilight.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.droidproger.byedpilight.PrefStore
import com.droidproger.byedpilight.R
import com.droidproger.byedpilight.data.CmdLineData
import com.droidproger.byedpilight.data.ViewCmdData
import com.droidproger.byedpilight.dataModel
import com.droidproger.byedpilight.ui.theme.ComposeAppTheme
import kotlinx.coroutines.launch

@Composable
fun CmdCard(cmdData: ViewCmdData, navController: NavController, prefStore: PrefStore){
    var backgroundColor = MaterialTheme.colorScheme.background
    var name = cmdData.cmdLineData.name
    var cmd = cmdData.cmdLineData.cmd
    if (name == null || cmd == null){
        return
    }
    if (cmd.equals(dataModel.cmdLine)){
        name = name+stringResource(R.string.current)
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer
    }
    val scope = rememberCoroutineScope()
    var checked by remember { mutableStateOf(cmdData.selected) }
    Column (
        Modifier.clickable( //selectable
            //selected = dataModel.selected[index],
            onClick = {
                dataModel.cmdLine = cmdData.cmdLineData.cmd
                tempCmdLine = cmdData.cmdLineData.cmd
                scope.launch {
                    prefStore.saveCmdLine(cmdData.cmdLineData.cmd)
                }
                navController.navigateUp()
            }
        ).background(backgroundColor)
    ){
        Row (
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            Alignment.CenterVertically
        ){
            Text(
                text = name,
                Modifier
                    .weight(1f)
                    .padding(10.dp, 0.dp, 0.dp, 0.dp),
                fontWeight = FontWeight.Bold
            )
            Checkbox(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    cmdData.selected = it
                },
            )
        }
        Text(
            text = cmdData.cmdLineData.cmd
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CmdPreview() {
    ComposeAppTheme {
        Surface {
            val cmdlineData = CmdLineData(
                "test",
                "--ip 192.168.0.1 --port 1080")
            val cmddata = ViewCmdData(
                cmdlineData,
                true
            )
            CmdCard(cmdData = cmddata,
                navController = rememberNavController(),
                prefStore = PrefStore(LocalContext.current))
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DarkCmdPreview() {
    ComposeAppTheme {
        Surface {
        val cmdlineData = CmdLineData(
            "test",
            "--ip 192.168.0.1 --port 1080")
        val cmddata = ViewCmdData(
            cmdlineData,
            true
        )
        CmdCard(cmdData = cmddata,
            navController = rememberNavController(),
            prefStore = PrefStore(LocalContext.current))
        }
    }
}