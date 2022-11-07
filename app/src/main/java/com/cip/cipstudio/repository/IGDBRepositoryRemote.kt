package com.cip.cipstudio.repository

import android.util.Log
import com.api.igdb.apicalypse.APICalypse
import com.api.igdb.apicalypse.Sort
import com.api.igdb.request.IGDBWrapper
import com.api.igdb.request.TwitchAuthenticator
import com.api.igdb.request.jsonGames
import com.cip.cipstudio.model.data.GameDetailsJson
import com.cip.cipstudio.utils.Converter
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject

object IGDBRepositoryRemote : IGDBRepository {

    private val TAG= "IGDBWrapper"
    private val CLIENT_ID = "fyniflwh4wnvh1ww0i139mwolan1g5"
    private val CLIENT_SECRET = "4ot4bg1eqxvb2syuko6cewv5ccsn6s"
    private var initialToken = false

    private val secondsInAWeek = 604800L
    private val secondsInADay = 86400L

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun init() {
        val job = GlobalScope.launch(Dispatchers.IO) {
            val token = TwitchAuthenticator.requestTwitchToken(CLIENT_ID, CLIENT_SECRET)
            Log.i(TAG, "Token: $token")
            IGDBWrapper.setCredentials(CLIENT_ID, token?.access_token.toString())
        }
        while (!job.isCompleted) {
            delay(100)
        }
        initialToken = true
    }

    override suspend fun getGamesMostHyped(): List<GameDetailsJson> =withContext(Dispatchers.IO) {
        if (!initialToken) {
            init()
        }
        val apicalypse = APICalypse().fields("name, id, cover.url")
            .where("cover != n & hypes != 0 & first_release_date > " + (System.currentTimeMillis() / 1000L))
            .sort("hypes", Sort.DESCENDING)
            .limit(10)

        val temp = Converter.fromJsonArrayToArrayList(JSONArray(IGDBWrapper.jsonGames(apicalypse)))
        return@withContext temp.map { jsonObject -> GameDetailsJson(jsonObject) }
    }

    override suspend fun getGamesMostRated(): List<GameDetailsJson> = withContext(Dispatchers.IO) {
        if (!initialToken) {
            init()
        }
        val apicalypse = APICalypse().fields("name, id, cover.url")
            .where("cover != n & total_rating_count >= 10 & total_rating != 0 & aggregated_rating != 0")
            .sort("total_rating", Sort.DESCENDING)
            .limit(10)
        val temp = Converter.fromJsonArrayToArrayList(JSONArray(IGDBWrapper.jsonGames(apicalypse)))
        return@withContext temp.map { jsonObject -> GameDetailsJson(jsonObject) }
    }

    override suspend fun getGamesDetails(gameId: Int): GameDetailsJson {
        if (!initialToken) {
            init()
        }
        val apicalypse = APICalypse().fields("name, summary, first_release_date," +
                "rating, rating_count, total_rating, total_rating_count" +
                "screenshots.url, genres.name, genres.id, platforms.name, platform.id" +
                "similar_games.name, similar_games.id, similar_games.cover.url")
            .where("id = $gameId")
        return GameDetailsJson(JSONObject(IGDBWrapper.jsonGames(apicalypse)))
    }


}