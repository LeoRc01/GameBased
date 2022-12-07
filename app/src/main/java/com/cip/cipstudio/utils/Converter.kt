package com.cip.cipstudio.utils

import android.util.Log
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.model.data.PlatformDetails
import org.json.JSONArray
import org.json.JSONObject
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar

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

        fun fromJsonArrayToYear(jsonArray: JSONArray) : Int{
            return fromTimestampToYear(
                jsonArray.getJSONObject(0).getLong("first_release_date")
            )
        }

        fun fromTimestampToYear(timestamp: Long) : Int{
            val cal : Calendar = Calendar.getInstance();
            cal.timeInMillis = timestamp*1000;
            return cal.get(Calendar.YEAR);

        }

        fun fromJsonArrayToListString(jsonArray: JSONArray, key: String): ArrayList<String> {
            val result = ArrayList<String>()
            for (i in 0 until jsonArray.length()) {
                result.add(jsonArray.getJSONObject(i).getString(key))
            }
            return result
        }
    }
}
