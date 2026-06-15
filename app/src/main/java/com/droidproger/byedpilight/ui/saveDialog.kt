package com.droidproger.byedpilight.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.droidproger.byedpilight.R
import com.droidproger.byedpilight.dataModel
import com.droidproger.byedpilight.ui.theme.ComposeAppTheme
import java.io.File

@Composable
fun SaveCmdToJsonDialog(){
    Dialog(onDismissRequest = {dataModel.saveToJson = false}){
        val context = LocalContext.current
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(Alignment.CenterVertically),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = stringResource(R.string.enterName),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    //.fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center,
            )
            val text = remember { mutableStateOf(dataModel.cmdName) }//
            TextField(
                value = text.value,
                onValueChange = {
                    dataModel.cmdName = it
                    text.value = it
                },
                Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .fillMaxWidth(),
                placeholder = {
                    Text(stringResource(R.string.paramSet))
                }
            )
            Row(
                Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                TextButton(
                    onClick = {
                        dataModel.saveToJson = false
                    },
                    Modifier
                        .padding(10.dp)
                        .weight(1f)
                ) {
                    Text(stringResource(R.string.cancel))
                }
                TextButton(
                    onClick = {
                        if (dataModel.cmdName == ""){
                            Toast.makeText(context, R.string.nameDontSet, Toast.LENGTH_SHORT).show()
                        }else {
                            val path = context.getFilesDir()
                            val file = File(path,dataModel.jsonName)
                            val str = dataModel.saveJson()
                            if (str.equals(dataModel.SET_EXIST)){
                                Toast.makeText(context, R.string.setExist, Toast.LENGTH_SHORT).show()
                            }else{
                                dataModel.savejsonToFile(file,str)
                            }
                            dataModel.saveToJson = false
                            dataModel.cmdName = ""
                        }
                    },
                    Modifier
                        .padding(10.dp)
                        .weight(1f)
                ) {
                    Text("Ok")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SaveDialogPreview(){
    ComposeAppTheme {
        SaveCmdToJsonDialog()
    }
}