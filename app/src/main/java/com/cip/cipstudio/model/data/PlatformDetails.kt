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
    val hardwareDetails: PlatformHardwareDetails?
) : java.io.Serializable {
    constructor(jsonGame: JSONObject) : this(
        jsonGame.getStringOrEmpty("id"),
        jsonGame.getStringOrEmpty("abbreviation"),
        jsonGame.getStringOrEmpty("alternative_name"),
        jsonGame.getStringOrEmpty("category"),
        jsonGame.getStringOrEmpty("name"),
        if (jsonGame.has("platform_logo")) jsonGame.getJSONObject("platform_logo")
            .getStringOrEmpty("url").getCorrectPlatformLogo() else "",
        jsonGame.getStringOrEmpty("summary"),
        jsonGame.getStringOrEmpty("url"),
        if (jsonGame.has("versions")) PlatformHardwareDetails(
            jsonGame.getJSONArray("versions").getJSONObject(0)
        ) else null
    ) {
        category = getCategoryString(if (category.isEmpty()) -1 else category.toInt())
        if (summary.isEmpty()) summary = "No summary provided."
        Log.i("-----", "------")
        Log.i("ID", id)
        Log.i("NAME", name)
        Log.i("-----", "------")
    }

    constructor(id : String, name : String) : this(
        id,
        "",
        "",
        "",
        name,
        "",
        "",
        "",
        null
    ){}

    fun getCategoryString(enumValue: Int): String {
        return when (enumValue) {
            1 -> "Console"
            2 -> "Arcade"
            3 -> "Platform"
            4 -> "Operating System"
            5 -> "Portable Console"
            6 -> "Computer"
            else -> {
                "Unknown"
            }
        }
    }
}
fun String.getCorrectPlatformLogo(): String {
    var result = this
    if (result.isNotEmpty()) {

        if (result.contains("t_thumb")) {
            result = result.replace("t_thumb", "t_logo_med")
            result = result.replace(".jpg", ".png")
        }

        result = "https:$result"
    }
    return result
}