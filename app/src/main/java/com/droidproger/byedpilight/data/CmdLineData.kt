package com.droidproger.byedpilight.data

import androidx.annotation.Keep
import kotlinx.serialization.Serializable
@Keep
@Serializable
class JsonString(
    val value: String
)
@Keep
@Serializable
data class CmdLineData(
    val name: String,
    val cmd: String
)

class ViewCmdData(
    val cmdLineData: CmdLineData,
    var selected: Boolean
)