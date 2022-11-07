package com.cip.cipstudio.utils

import org.json.JSONObject

class Converter{
    companion object {
        fun fromJsonObjectFieldToArrayList(jsonObject: org.json.JSONObject, field: String): ArrayList<JSONObject> {
            val temp = jsonObject.get(field) as org.json.JSONArray
            val result = ArrayList<JSONObject>()
            for (i in 0 until temp.length()) {
                result.add(temp.getJSONObject(i))
            }
            return result
        }

        fun fromJsonArrayToArrayList(jsonArray: org.json.JSONArray): ArrayList<JSONObject> {
            val result = ArrayList<JSONObject>()
            for (i in 0 until jsonArray.length()) {
                result.add(jsonArray.getJSONObject(i))
            }
            return result
        }
    }
}
