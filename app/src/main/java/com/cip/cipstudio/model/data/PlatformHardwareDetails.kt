package com.cip.cipstudio.model.data

import android.content.res.Resources
import android.util.Log
import com.cip.cipstudio.R
import com.cip.cipstudio.utils.getStringOrNotDefined
import org.json.JSONObject

data class PlatformHardwareDetails(
    var id : String,
    var cpu : String,
    var graphics : String,
    var memory  : String,
    var output : String,
    var storage : String,
    var resolutions : String
) : java.io.Serializable{
    constructor(jsonObject: JSONObject) : this(
       jsonObject.getStringOrNotDefined("id"),
        jsonObject.getStringOrNotDefined("cpu"),
        jsonObject.getStringOrNotDefined("graphics"),
        jsonObject.getStringOrNotDefined("memory"),
        jsonObject.getStringOrNotDefined("output"),
        jsonObject.getStringOrNotDefined("storage"),
        jsonObject.getStringOrNotDefined("resolutions")
    ){
        Log.i("HARDWARE", jsonObject.toString())
    }


}