package com.droidproger.byedpilight


import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.droidproger.byedpilight.data.CmdLineData
import com.droidproger.byedpilight.data.JsonString
import com.droidproger.byedpilight.data.ServiceStatus
import com.droidproger.byedpilight.data.ViewCmdData
import com.droidproger.byedpilight.ui.tempCmdLine
import com.droidproger.byedpilight.utility.NetworkReceiver
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.File
import java.io.IOException
import kotlin.collections.arrayListOf

@SuppressLint("MutableCollectionMutableState")
class DataModel: ViewModel() {

    var stoppedManually = false//by mutableStateOf(false)
    var startedManually = false
    var anyConn by mutableStateOf(false)
    var mobile by mutableStateOf(false)
    var textMinLines: Int = 3
    var isCmdEdit by mutableStateOf(false)
    var cmdLine by mutableStateOf("")
    var isPortEdit by mutableStateOf(false)
    var proxyPort by mutableIntStateOf(value = 1080)

    var isDnsEdit by mutableStateOf(false)
    var dnsIp by mutableStateOf(value = "1.1.1.1")
    var ipv6enabled  by mutableStateOf(value = false)
    var showAbout by mutableStateOf(value = false)
    var showWarn by mutableStateOf(value = false)
    var saveToJson by mutableStateOf(value = false)
    var cmdName by mutableStateOf("")
    var cmdList = arrayListOf<CmdLineData>()
    var cmdView = mutableStateListOf<ViewCmdData>() //
    val receiver = NetworkReceiver()
    var receiverRegistered by mutableStateOf(value = false)
    val jsonName = "cmdlines.json"
    var serviceStatus by mutableStateOf(value = ServiceStatus.Disconnected)
    val SET_EXIST = "set_exist"
    fun btnTextRes(): Int{
        when(serviceStatus) {
            ServiceStatus.Disconnected -> return R.string.connect
            ServiceStatus.Connected -> return R.string.disconnect
            ServiceStatus.Failed -> return R.string.connect
        }
    }

    fun statusTextRes(): Int{
        when(serviceStatus) {
            ServiceStatus.Disconnected -> return R.string.disconnected
            ServiceStatus.Connected -> return R.string.connected
            ServiceStatus.Failed -> return R.string.disconnected
        }
    }

    fun saveJson(): String{
        var cmdtoSave: String
        if (isCmdEdit){
            cmdtoSave = tempCmdLine
        }else{
            cmdtoSave = cmdLine
        }
        val cmdLineData = CmdLineData(cmdName,cmdtoSave)
        if (!cmdExists(cmdLineData)){ //cmdList.contains(cmdLineData)
            val index = cmdList.size
            cmdList.add(index,cmdLineData)
            addCmdView(index,cmdLineData)
        }else{
            // TODO: toast "this set of cmd is already saved"
            return SET_EXIST
        }
        return createJsonStr(cmdList)
    }

    fun createJsonStr(list: ArrayList<CmdLineData>): String{
        val gson = Gson()
        val jsonString = JsonString(gson.toJson(list))
        return jsonString.value
    }

    fun loadJson(jsonString: String){
        val gson = Gson()
        //val token = object : TypeToken<ArrayList<CmdLineData>>() {}.type
        try {
            val tempCmdList:Array<CmdLineData> = gson.fromJson(jsonString, Array<CmdLineData>::class.java)//
            for(i in 0..tempCmdList.size-1){
                val tempCmdData: CmdLineData = tempCmdList[i]
                val name = tempCmdData.name
                val cmd = tempCmdData.cmd
                if (tempCmdData.name == null || tempCmdData.cmd == null){
                    continue
                }
                if (!cmdExists(tempCmdData)){
                    val index = cmdList.size
                    cmdList.add(index,tempCmdData)
                    addCmdView(index,cmdList[index])
                }
            }
        }catch (e: JsonSyntaxException){
            e.printStackTrace()
        }catch (e: Error){
            e.printStackTrace()
        }
    }

    fun cmdExists(cmdData: CmdLineData): Boolean{
        for(i in 0..cmdList.size-1) {
            if (cmdData.cmd.equals(cmdList[i].cmd)){
                return true
            }
        }
        return false
    }

    fun addCmdView(index: Int, cmdLineData: CmdLineData){
        val cmdVeiwData = ViewCmdData(cmdLineData,false)//
        cmdView.add(index,cmdVeiwData)
    }

    fun savejsonToFile(file: File, str: String){
        //
        if (str.isEmpty()){
            return
        }
        try {
            file.writeText(str)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}