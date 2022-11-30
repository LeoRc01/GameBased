package com.cip.cipstudio.utils

import android.util.Log
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.model.data.PlatformDetails
import org.json.JSONArray
import org.json.JSONObject

class Converter{
    companion object {
        fun fromJsonObjectToArrayList(jsonObject: JSONObject, field: String): ArrayList<JSONObject> {
            if (!jsonObject.has(field)) {
                return ArrayList()
            }
            return fromJsonArrayToArrayList(jsonObject.getJSONArray(field))
        }

        fun fromJsonArrayToArrayList(jsonArray: JSONArray): ArrayList<JSONObject> {
            val result = ArrayList<JSONObject>()
            for (i in 0 until jsonArray.length()) {
                result.add(jsonArray.getJSONObject(i))
            }
            return result
        }

        fun fromJsonObjectToGameDetailsArrayList(jsonObject: JSONObject, field: String): ArrayList<GameDetails> {
            if (!jsonObject.has(field)) {
                return ArrayList()
            }
            return fromJsonArrayToGameDetailsArrayList(jsonObject.getJSONArray(field))
        }

        fun fromJsonArrayToGameDetailsArrayList(jsonArray: JSONArray): ArrayList<GameDetails> {
            val result = ArrayList<GameDetails>()
            for (i in 0 until jsonArray.length()) {
                result.add(GameDetails(jsonArray.getJSONObject(i)))
            }

            return result
        }

        fun fromJsonArrayToPlatformDetailsArrayList(jsonArray: JSONArray): ArrayList<PlatformDetails> {
            val result = ArrayList<PlatformDetails>()
            for (i in 0 until jsonArray.length()) {
                result.add(PlatformDetails(jsonArray.getJSONObject(i)))
            }
            return result
        }
    }
}
