package com.cip.cipstudio.model.data

import android.util.Log
import com.cip.cipstudio.utils.*
import org.json.JSONObject

data class PlatformDetails(
    var id : String,
    var abbreviation : String,
    var alternativeName : String,
    var category : String,
    var name : String,
    var platformLogo : String,
    var summary : String,
    var url : String,
) {
    constructor(jsonGame: JSONObject) : this(
        jsonGame.getJSONArray("platforms").getJSONObject(0).getStringOrEmpty("id"),
        jsonGame.getJSONArray("platforms").getJSONObject(0).getStringOrEmpty("abbreviation"),
        jsonGame.getJSONArray("platforms").getJSONObject(0).getStringOrEmpty("alternative_name"),
        jsonGame.getJSONArray("platforms").getJSONObject(0).getString("category"),
        jsonGame.getJSONArray("platforms").getJSONObject(0).getStringOrEmpty("name"),
        jsonGame.getJSONArray("platforms").getJSONObject(0).getJSONObject("platform_logo").getStringOrEmpty("url").getCorrectPlatformLogo(),
        jsonGame.getJSONArray("platforms").getJSONObject(0).getStringOrEmpty("summary"),
        jsonGame.getJSONArray("platforms").getJSONObject(0).getStringOrEmpty("url"),
    ){
        //Log.i("DATA", jsonGame.toString())
        category = getCategoryString(category.toInt())
        if(summary.isEmpty()) summary = "No summary provided."
    }
}

fun getCategoryString(enumValue : Int) : String{
    return when (enumValue) {
        1 -> "Console"
        2 -> "Arcade"
        3 -> "Platform"
        4 -> "Operating System"
        5 -> "Portable Console"
        6 -> "Computer"
        else -> {"Unknown"}
    }
}

fun String.getCorrectPlatformLogo() : String{
    var result = this
    if (!result.isEmpty()) {
        /*
        if(result.contains("t_thumb")){
            result = this.replace("t_thumb", "t_logo_med")
        }
         */
        result = "https:$result"
    }
    return result
}