package com.cip.cipstudio.model.data.util

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*



fun Int.toDate() : String{
    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    val date = dateFormat.format(Date(this.toLong() * 1000))
    return date
}

fun JSONObject.getIntField(name : String) : Int?{
    if(this.has(name)){
        return this.get(name) as Int
    }
    return null
}

fun JSONObject.getDoubleField(name : String) : Double?{
    if(this.has(name)){
        return this.get(name) as Double
    }
    return null
}

fun JSONObject.getStringField(name : String) : String?{
    if(this.has(name)){
        return this.get(name).toString()
    }
    return null
}

fun JSONObject.getDateField(name : String) : String?{
    if(this.has(name)){
        return (this.get(name)as Int).toDate()
    }
    return null
}




