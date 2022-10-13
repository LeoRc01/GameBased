package com.cip.cipstudio.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.http2.Header
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class IGDBRepository {

    private val CLIENT_ID : String = "fyniflwh4wnvh1ww0i139mwolan1g5"
    private val CLIENT_SECRET : String = "4ot4bg1eqxvb2syuko6cewv5ccsn6s"
    private val okHttpClient = OkHttpClient()

    val ACCESS_TOKEN : MutableLiveData<String?> by lazy {
        MutableLiveData<String?>()
    }

    private val authUrl = "https://id.twitch.tv/oauth2/token?client_id=${CLIENT_ID}&client_secret=${CLIENT_SECRET}&grant_type=client_credentials"

    private fun generateAccessToken(){
        val payload = ""
        val request = Request.Builder()
            .url(authUrl)
            .post(payload.toRequestBody())
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                throw Exception(e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle this
                response.use {
                    if(!response.isSuccessful){
                        Log.i("Error", "Something went wrong.")
                    }else{
                        val jsonString : String = response.body!!.string()
                        val json = JSONObject(jsonString)
                        ACCESS_TOKEN.postValue(json.getString("access_token"))
                    }
                }
            }
        })
    }

    fun getGame(){
        if(ACCESS_TOKEN.value==null){
            throw Exception("ACCESS_TOKEN is null")
        }
        val payload = "fields *; where id = 1942;"

        val clientHeader = Header("Client-ID", CLIENT_ID)
        val accessTokenHeader = Header("Authorization", "Bearer s9kmy0ity8ljiake9en6g1dar5nbjd")

        val request = Request.Builder()
            .url("https://api.igdb.com/v4/games")
            .post(payload.toRequestBody())
            .header("Client-ID", CLIENT_ID)
            .header("Authorization", "Bearer ${ACCESS_TOKEN.value!!}")

            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                throw Exception(e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle this
                response.use {
                    if(!response.isSuccessful){
                        Log.i("Error", response.headers.toString())
                    }else{
                        val jsonString : String = response.body!!.string()
                        val json = JSONArray(jsonString)
                        Log.i("ASD", jsonString)
                    }
                }
            }
        })
    }



    fun getClientID() : String{
        return CLIENT_ID
    }

    init {
        generateAccessToken()
    }

}