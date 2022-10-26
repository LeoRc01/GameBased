package com.cip.cipstudio.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cip.cipstudio.model.data.Game
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.sql.Timestamp

class IGDBRepository {

    private val CLIENT_ID : String = "fyniflwh4wnvh1ww0i139mwolan1g5"
    private val CLIENT_SECRET : String = "4ot4bg1eqxvb2syuko6cewv5ccsn6s"
    private val okHttpClient = OkHttpClient()

    companion object{
        val ACCESS_TOKEN : MutableLiveData<String?> by lazy {
            MutableLiveData<String?>()
        }
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
                        Log.i("TOKEN", ACCESS_TOKEN.value.toString())
                    }
                }
            }
        })
    }


    fun getGamesByPayload(payload : String,onSuccess: (ArrayList<Game>)->Unit){
        if(ACCESS_TOKEN.value==null){
            throw Exception("ACCESS_TOKEN is null")
        }

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
                        // Prendo l'array di items
                        val json = JSONArray(jsonString)
                        val games : ArrayList<Game> = ArrayList<Game>()
                        // Looppo su tutta la lista dei jsonObjects
                        (0 until json.length()).forEach {
                            val item = json.getJSONObject(it)
                            val platformsIds : ArrayList<Int> = arrayListOf()
                            (0 until (item.get("platforms") as JSONArray).length()).forEach{
                                val plat = (item.get("platforms") as JSONArray).get(it) as Int
                                platformsIds.add(plat)
                            }


                            val game = Game(item.get("name").toString(),
                                            item.get("summary").toString(),
                                            item.get("first_release_date") as Int,
                                            item.get("rating") as Double,
                                            item.get("rating_count") as Int,
                                            item.get("total_rating") as Double,
                                            item.get("total_rating_count") as Int,
                                            platformsIds,
                                            item.get("id") as Int,
                                )
                            games.add(game)
                        }
                        onSuccess.invoke(games)
                    }
                }
            }
        })
    }

    fun getPlatforms(platformId : Int, onSuccess: (String)->Unit){
        if(ACCESS_TOKEN.value==null){
            throw Exception("ACCESS_TOKEN is null")
        }
        val payload = "fields name, abbreviation, url; where id = ${platformId};"

        val request = Request.Builder()
            .url("https://api.igdb.com/v4/platforms")
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

                        try {
                            val jsonString : String = response.body!!.string()
                            val jsonArray = JSONArray(jsonString)

                            if(jsonArray.getJSONObject(0).has("abbreviation")) {
                                onSuccess.invoke(
                                    jsonArray.getJSONObject(0).getString("abbreviation")
                                )
                            }else{
                                onSuccess.invoke(
                                    jsonArray.getJSONObject(0).getString("name")
                                )
                            }

                        }catch (e : Exception){
                            throw e
                        }

                    }
                }
            }
        })
    }

    fun getGameCover(gameId : Int, onSuccess:(String) -> Unit){
        if(ACCESS_TOKEN.value==null){
            throw Exception("ACCESS_TOKEN is null")
        }
        val payload = "fields url; where game = ${gameId};"

        val request = Request.Builder()
            .url("https://api.igdb.com/v4/covers")
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
                        try {
                            val jsonString : String = response.body!!.string()
                            val json = JSONArray(jsonString)
                            val cover : JSONObject= json[0] as JSONObject;
                            onSuccess.invoke(cover.get("url").toString())
                        }catch (e : Exception){
                            onSuccess.invoke("NO_COVER")
                        }

                    }
                }
            }
        })


    }



    fun getClientID() : String{
        return CLIENT_ID
    }

    constructor(generate : Boolean = true) {
        if(generate)
            generateAccessToken()
    }


}