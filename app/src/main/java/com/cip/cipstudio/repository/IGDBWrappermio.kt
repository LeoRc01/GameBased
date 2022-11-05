package com.cip.cipstudio.repository

import android.util.Log
import com.api.igdb.apicalypse.APICalypse
import com.api.igdb.apicalypse.Sort
import com.api.igdb.exceptions.RequestException
import com.api.igdb.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import proto.Game
import proto.Platform

object IGDBWrappermio {

    private val TAG= "IGDBWrapper"
    private val CLIENT_ID = "fyniflwh4wnvh1ww0i139mwolan1g5"
    private val CLIENT_SECRET = "4ot4bg1eqxvb2syuko6cewv5ccsn6s"

   suspend fun init() = withContext(Dispatchers.IO) {
       val token = TwitchAuthenticator.requestTwitchToken(CLIENT_ID, CLIENT_SECRET)
       Log.i(TAG, "Token: $token")
       IGDBWrapper.setCredentials(CLIENT_ID, token?.access_token.toString())
   }

    // API per i giochi in ordine decrescente per data di uscita
    suspend fun games(): List<Game> = withContext(Dispatchers.IO)  {
        val apicalypse = APICalypse().fields("*").sort("release_dates.date", Sort.DESCENDING).limit(10)
        var games : List<Game> = emptyList()
        try{
            games = IGDBWrapper.games(apicalypse)

        } catch(e: RequestException) {
          Log.e(TAG, "Error: ${e.message}")
        }
        return@withContext games
    }

    // API per le piattaforme, con input l'id della piattaforma
    suspend fun platformsGames(id : String) : List<Platform> = withContext(Dispatchers.IO) {
        val apicalypse = APICalypse().fields("*").where("id=$id")
        var platforms : List<Platform> = emptyList()
        platforms = IGDBWrapper.platforms(apicalypse)
        return@withContext platforms
    }

    // multiquery -> https://api-docs.igdb.com/#multi-query
    suspend fun prova() : String = withContext(Dispatchers.IO) {
        val apicalypse = APICalypse()
            .fields("name, platforms.name")
            .where("platforms !=n & platforms = {48}")
            .limit(2)
        return@withContext IGDBWrapper.jsonGames(apicalypse)
    }

}