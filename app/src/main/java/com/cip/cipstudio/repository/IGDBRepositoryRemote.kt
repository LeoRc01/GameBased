package com.cip.cipstudio.repository

import android.util.Log
import com.api.igdb.apicalypse.APICalypse
import com.api.igdb.apicalypse.Sort
import com.api.igdb.exceptions.RequestException
import com.api.igdb.request.IGDBWrapper
import com.api.igdb.request.TwitchAuthenticator
import com.api.igdb.request.jsonGames
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.utils.Converter
import com.cip.cipstudio.utils.GameTypeEnum
import kotlinx.coroutines.*
import org.json.JSONArray

object IGDBRepositoryRemote : IGDBRepository {

    private val TAG= "IGDBWrapper"
    private val CLIENT_ID = "fyniflwh4wnvh1ww0i139mwolan1g5"
    private val CLIENT_SECRET = "4ot4bg1eqxvb2syuko6cewv5ccsn6s"
    private var isInitialized = false

    private val secondsInAWeek = 604800L
    private val secondsInADay = 86400L

    private fun init() {
        runBlocking {
            if(!isInitialized) {
                Log.i(TAG, "init: Initializing IGDBWrapper")
                val token = TwitchAuthenticator.requestTwitchToken(CLIENT_ID, CLIENT_SECRET)
                Log.d(TAG, "Token: $token")
                IGDBWrapper.setCredentials(CLIENT_ID, token?.access_token.toString())
                isInitialized = true
            }
        }
    }

    private suspend fun makeRequest(request: () -> String, attempt : Int = 0):
            JSONArray = withContext(Dispatchers.IO) {
        init()

        var json : String = ""

        try {
            Log.i(TAG, "Making request, attempt: $attempt")
            json = request.invoke()
        } catch (RequestException: RequestException) {
            if (RequestException.statusCode == 401 && attempt <= 10) {
                Log.i(TAG, "Token expired, refreshing...")
                runBlocking {
                    isInitialized = false
                    init()
                }
                return@withContext makeRequest(request)
            }
            else {
                if (attempt > 10)
                    Log.e(TAG, "makeRequest: Too many attempts, aborting")
                Log.e(TAG, "Request failed, status code: ${RequestException.statusCode}")
                Log.e(TAG, "Request failed, message: ${RequestException.message}")
                throw RequestException
            }
        }
        Log.d(TAG, "request successful")
        return@withContext JSONArray(json)
    }

    private suspend fun getGamesMostHyped(): List<GameDetails> = withContext(Dispatchers.IO) {
        val apicalypse = APICalypse().fields("name, id, cover.url")
            .where("cover != n & hypes != 0 & first_release_date > " + (System.currentTimeMillis() / 1000L))
            .sort("hypes", Sort.DESCENDING)
            .limit(10)
        val json = makeRequest ({ IGDBWrapper.jsonGames(apicalypse) })
        return@withContext Converter.fromJsonArrayToGameDetailsArrayList(json)
    }

    private suspend fun getGamesMostRated(): List<GameDetails> = withContext(Dispatchers.IO) {
        val apicalypse = APICalypse().fields("name, id, cover.url")
            .where("cover != n & total_rating_count >= 10 & total_rating != 0 & aggregated_rating != 0")
            .sort("total_rating", Sort.DESCENDING)
            .limit(10)
        val json = makeRequest ({ IGDBWrapper.jsonGames(apicalypse) })
        return@withContext Converter.fromJsonArrayToGameDetailsArrayList(json)
    }

    // TODO: Add multiplayer_modes to the request (forse)
    // TODO: Add dlcs to the request (forse)
    override suspend fun getGameDetails(gameId: String): GameDetails = withContext(Dispatchers.IO) {
        val apicalypse = APICalypse().fields("id, name, summary, first_release_date, cover.url," +
                "rating, rating_count, total_rating, total_rating_count," +
                "screenshots.url, genres.name, genres.id, platforms.name, platforms.id," +
                "similar_games.name, similar_games.id, similar_games.cover.url," +
                "involved_companies.company.name, involved_companies.developer," +
                "involved_companies.publisher, involved_companies.supporting," +
                "involved_companies.porting, franchise.name, franchise.id," +
                "game_modes.name, player_perspectives.name, language_supports.language.name," +
                "collection.name, collection.id, parent_game.name, parent_game.id," +
                "dlcs.name, dlcs.id, dlcs.cover.url")
            .where("id = $gameId")
        val json = makeRequest ({ IGDBWrapper.jsonGames(apicalypse) })
        return@withContext GameDetails(json.getJSONObject(0))
    }

    override suspend fun getGamesByIds(gameIds: ArrayList<String>): List<GameDetails> = withContext(Dispatchers.IO) {
        val apicalypse = APICalypse()
            .fields("name, id, cover.url")
            .where("id = ${gameIds.toString().replace("[", "(").replace("]", ")")}")
        val json = makeRequest ({ IGDBWrapper.jsonGames(apicalypse) })
        return@withContext Converter.fromJsonArrayToGameDetailsArrayList(json)

    }

    override suspend fun getGamesByType(type: GameTypeEnum): List<GameDetails> = withContext(Dispatchers.IO) {
        return@withContext when (type) {
            GameTypeEnum.MOST_HYPED -> getGamesMostHyped()
            GameTypeEnum.MOST_RATED -> getGamesMostRated()
        }
    }

}