package com.droidproger.byedpilight.ui

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.droidproger.byedpilight.PrefStore
import com.droidproger.byedpilight.R
import com.droidproger.byedpilight.data.CmdLineData
import com.droidproger.byedpilight.dataModel
import com.droidproger.byedpilight.ui.theme.ComposeAppTheme
import java.io.File
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedCmdScreen(navController: NavController, prefStore: PrefStore){
    val context = LocalContext.current
    val path = context.getFilesDir()
    val file = File(path,dataModel.jsonName)
    var text = ""
    if (!file.exists()){
        if (!file.createNewFile()){
            Toast.makeText(context, R.string.fileNotCreated, Toast.LENGTH_SHORT).show()
        }
    }else{
        try {
            text = file.readText()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    dataModel.loadJson(text)
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        if (it != null){
            importCmd(context,it.normalizeScheme())
        }
    }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/json")){
        if (it != null){
            val list = arrayListOf<CmdLineData>()
            val c = dataModel.cmdView.size-1
            for (i in 0..c){
                if (dataModel.cmdView[i].selected){
                    list.add(dataModel.cmdList[i])
                }
            }
            if (!list.isEmpty()){
                exportCmd(context,it.normalizeScheme(),list)
            }
        }
    }
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Surface {
        Column (
            Modifier
                .fillMaxSize()
                .padding(bottom = bottomPadding)
        ){
            TopAppBar(
                title = {
                    Text(stringResource(R.string.savedParams))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
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
            // list
            LazyColumn (
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ){
                itemsIndexed(dataModel.cmdView)
                {   index,
                    cmdLineData -> CmdCard(cmdLineData,navController,prefStore)
                }
            }
            Row (
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            )
            {
                TextButton(
                    onClick = {
                        importLauncher.launch(arrayOf("text/plain","application/json"))
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowUp,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(R.string.imporT)
                        )
                    }

                }
                TextButton(
                    onClick = {
                        exportLauncher.launch("DpiExport.json")
                    },
                    Modifier.padding(start = 8.dp, end = 8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(R.string.export)
                        )
                    }

                }
                TextButton(
                    onClick = {
                        deleteCmd(context)
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(R.string.delete)
                        )
                    }

                }
            }
        }
    }

}

fun importCmd(context: Context, uri: Uri){
    val fis = context.contentResolver.openInputStream(uri)
    if (fis != null) {
        val byteArray = fis.readBytes()
        fis.close()
        if (byteArray.size>0){
            val jsonStr = byteArray.decodeToString()
            dataModel.loadJson(jsonStr)
            saveToFile(context)
        }
    }
}

fun exportCmd(context: Context,uri: Uri, list: ArrayList<CmdLineData>){//file: File
    val jsonstr = dataModel.createJsonStr(list)
    val fos = context.contentResolver.openOutputStream(uri)
    if (fos != null) {
        fos.write(jsonstr.toByteArray())
        fos.close()
    }
}

fun deleteCmd(context: Context){
    val c = dataModel.cmdView.size-1
    for (i in c downTo 0){
        if (dataModel.cmdView[i].selected){
            dataModel.cmdList.removeAt(i)
            dataModel.cmdView.removeAt(i)
        }
    }
    saveToFile(context)
}

fun saveToFile(context: Context){
    val path = context.getFilesDir()
    val file = File(path,dataModel.jsonName)
    dataModel.savejsonToFile(file, dataModel.createJsonStr(dataModel.cmdList))
}

@Preview(showBackground = true)
@Composable
fun SavedCmdPreview() {
    ComposeAppTheme {
        SavedCmdScreen(navController = rememberNavController(), prefStore = PrefStore(LocalContext.current))
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DarkSavedCmdPreview() {
    ComposeAppTheme {
        SavedCmdScreen(navController = rememberNavController(), prefStore = PrefStore(LocalContext.current))
    }
}