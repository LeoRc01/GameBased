package com.cip.cipstudio.utils

import android.content.res.Resources
import com.cip.cipstudio.R
import com.cip.cipstudio.model.data.GameDetails
import org.json.JSONObject
import kotlin.math.roundToInt

fun JSONObject.getStringOrEmpty(field: String) : String {
    if (this.has(field)) {
        return this.getString(field)
    } else {
        return ""
    }
}

 fun JSONObject.getDoubleOrEmpty(field: String) : String {
    if (this.has(field)) {
        return this.getDouble(field).roundToInt().toString()
    } else {
        return "0"
    }
}

 fun JSONObject.getArrayListOrEmpty(field: String) : List<JSONObject> {
    return Converter.fromJsonObjectToArrayList(this, field)
}

 fun JSONObject.getGameDetailsArrayListOrEmpty(field: String) : List<GameDetails> {
    return Converter.fromJsonObjectToGameDetailsArrayList(this, field)
}

 fun JSONObject.getIntOrZero(field: String) : String {
    if (this.has(field)) {
        return this.getInt(field).toString()
    } else {
        return "0"
    }
}

fun JSONObject.getStringOrNotDefined(field : String) : String {
    if (this.has(field)) {
        return this.getString(field)
    }
    return "N/D"
}