package com.cip.cipstudio.repository

import android.util.Log
import com.api.igdb.apicalypse.APICalypse
import com.api.igdb.apicalypse.Sort
import com.api.igdb.request.IGDBWrapper
import com.api.igdb.request.TwitchAuthenticator
import com.api.igdb.request.jsonGames
import kotlinx.coroutines.*
import org.json.JSONArray

object IGDBRepositoryRemote : IGDBRepository {

    private val TAG= "IGDBWrapper"
    private val CLIENT_ID = "fyniflwh4wnvh1ww0i139mwolan1g5"
    private val CLIENT_SECRET = "4ot4bg1eqxvb2syuko6cewv5ccsn6s"
    private var initialToken = false

    suspend fun init() = withContext(Dispatchers.IO) {

    }

    override suspend fun getGamesMostHyped(): JSONArray =withContext(Dispatchers.IO) {
        if (!initialToken) {
            val token = TwitchAuthenticator.requestTwitchToken(CLIENT_ID, CLIENT_SECRET)
            Log.i(TAG, "Token: $token")
            IGDBWrapper.setCredentials(CLIENT_ID, token?.access_token.toString())
            initialToken = true
        }
        val apicalypse = APICalypse().fields("name, id, cover.url")
            .where("cover != n & hypes != 0 & first_release_date > " + (System.currentTimeMillis() / 1000L))
            .sort("hypes", Sort.DESCENDING)
            .limit(10)
        return@withContext JSONArray(IGDBWrapper.jsonGames(apicalypse))
    }

    override suspend fun getGamesMostRated(): JSONArray = withContext(Dispatchers.IO) {
        if (!initialToken) {
            val token = TwitchAuthenticator.requestTwitchToken(CLIENT_ID, CLIENT_SECRET)
            Log.i(TAG, "Token: $token")
            IGDBWrapper.setCredentials(CLIENT_ID, token?.access_token.toString())
            initialToken = true
        }
        val apicalypse = APICalypse().fields("name, id, cover.url")
            .where("cover != n & total_rating_count >= 10 & total_rating != 0 & aggregated_rating != 0")
            .sort("total_rating", Sort.DESCENDING)
            .limit(10)
        return@withContext JSONArray(IGDBWrapper.jsonGames(apicalypse))
    }


}