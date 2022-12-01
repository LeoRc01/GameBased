package com.cip.cipstudio.dataSource.repository.IGDBRepositoryImpl

import android.util.Log
import com.api.igdb.apicalypse.APICalypse
import com.api.igdb.apicalypse.Sort
import com.api.igdb.exceptions.RequestException
import com.api.igdb.request.IGDBWrapper
import com.api.igdb.request.TwitchAuthenticator
import com.api.igdb.request.jsonGames
import com.api.igdb.request.jsonPlatforms
import com.cip.cipstudio.dataSource.repository.IGDBRepository
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.model.data.PlatformDetails
import com.cip.cipstudio.utils.Converter
import com.cip.cipstudio.utils.GameTypeEnum
import com.mayakapps.lrucache.LruCache
import kotlinx.coroutines.*
import org.json.JSONArray


object IGDBRepositoryRemote : IGDBRepository {

    private val TAG= "IGDBWrapper"
    private val CLIENT_ID = "fyniflwh4wnvh1ww0i139mwolan1g5"
    private val CLIENT_SECRET = "4ot4bg1eqxvb2syuko6cewv5ccsn6s"
    private var isInitialized = false

    private val cache = LruCache<String, JSONArray>(100)

    private val secondsInAWeek = 604800L
    private val secondsInADay = 86400L
    private val secondsInAMonth = 2592000L

    private fun init() {
        synchronized(this) {
            if(!isInitialized) {
                Log.i(TAG, "init: Initializing IGDBWrapper")
                val token = TwitchAuthenticator.requestTwitchToken(CLIENT_ID, CLIENT_SECRET)
                Log.d(TAG, "Token: $token")
                IGDBWrapper.setCredentials(CLIENT_ID, token?.access_token.toString())
                isInitialized = true
            }

        }
    }

    private suspend fun makeRequest(request: () -> String, key: String, refresh: Boolean):
            JSONArray = withContext(Dispatchers.IO) {
        init()

        var json : JSONArray? = null
        if (!refresh) {
            Log.i(TAG, "Try to retrieve data from cache")
            json = retrieveDataFromCache(key)
        }
        if (json == null) {
            Log.i(TAG, if (refresh) "Refreshing data" else "Data not found in cache")
            Log.i(TAG, "Try to retrieve data from IGDB")
            json = retrieveDataFromRemote( request )
            Log.i(TAG, "Data retrieved from IGDB")
            Log.i(TAG, "Try to save data in cache")
            saveDataInCache(key, json)
            Log.i(TAG, "Data saved in cache")
        } else {
            Log.i(TAG, "Data retrieved from cache")
        }

        return@withContext json

    }

    private suspend fun retrieveDataFromCache(key: String) : JSONArray? {
        Log.i(TAG, "Retrieving data from cache")
        return cache.get(key)
    }

    private suspend fun saveDataInCache(key: String, json: JSONArray) {
        Log.i(TAG, "Saving data in cache")
        cache.put(key, json)
    }

    private suspend fun retrieveDataFromRemote(request: () -> String, attempt : Int = 0):
            JSONArray = withContext(Dispatchers.IO) {
        var json = ""
        try {
            Log.i(TAG, "Retrieving data from remote")
            Log.i(TAG, "Making request, attempt: $attempt")
            json = request.invoke()
        } catch (RequestException: RequestException) {
            if (RequestException.statusCode == 401 && attempt <= 10) {
                Log.i(TAG, "Token expired, refreshing...")
                runBlocking {
                    isInitialized = false
                    init()
                }
                return@withContext retrieveDataFromRemote(request, attempt + 1)
            }
            else {
                if (attempt > 10)
                    Log.e(TAG, "retrieveDataFromRemote: Too many attempts, aborting")
                Log.e(TAG, "Request failed, status code: ${RequestException.statusCode}")
                Log.e(TAG, "Request failed, message: ${RequestException.message}")
                throw RequestException
            }
        }
        Log.d(TAG, "request successful")
        return@withContext JSONArray(json)
    }

    suspend fun getGamesByCollectionName(collectionName : String,refresh: Boolean): List<GameDetails> = withContext(Dispatchers.IO) {
        val apicalypse = APICalypse().fields("name, id, cover.url")
            .where("collection.name = \"$collectionName\"")
        val json = makeRequest ({ IGDBWrapper.jsonGames(apicalypse) }, "getGamesByCollectionName${collectionName}", refresh)
        Log.i("QUERY", apicalypse.buildQuery())
        return@withContext Converter.fromJsonArrayToGameDetailsArrayList(json)
    }
    override suspend fun getPlatformsInfo(platformIds : List<String>, refresh: Boolean) : List<PlatformDetails> = withContext(Dispatchers.IO){
        val platformIdsString = buildIdsForRequest(platformIds)
        val apicalypse = APICalypse().fields("abbreviation, " +
                "alternative_name, " +
                "category, " +
                "name, " +
                "platform_logo.url, " +
                "summary, " +
                "url, " +
                "id, " +
                "versions.cpu, " +
                "versions.graphics," +
                "versions.memory, " +
                "versions.output," +
                "versions.storage," +
                "versions.resolutions ")
            .where("id = $platformIdsString")
        val json = makeRequest ({ IGDBWrapper.jsonPlatforms(apicalypse) }, "getPlatformsInfo$platformIdsString", refresh)
        return@withContext Converter.fromJsonArrayToPlatformDetailsArrayList(json)
    }

    override suspend fun getGameDetails(gameId: String, refresh: Boolean): GameDetails = withContext(Dispatchers.IO){
        val apicalypse = APICalypse().fields("id, name, summary, first_release_date, cover.url," +
                "aggregated_rating, aggregated_rating_count, total_rating, total_rating_count," +
                "screenshots.url, genres.name, genres.id, platforms.name, platforms.id," +
                "similar_games.name, similar_games.id, similar_games.cover.url," +
                "involved_companies.company.name, involved_companies.developer," +
                "involved_companies.publisher, involved_companies.supporting," +
                "involved_companies.porting, franchise.name, franchise.id," +
                "game_modes.name, player_perspectives.name, language_supports.language.name," +
                "collection.name, collection.id, parent_game.name, parent_game.id," +
                "dlcs.name, dlcs.id, dlcs.cover.url")
            .where("id = $gameId")
        val json = makeRequest ({ IGDBWrapper.jsonGames(apicalypse) }, "getGameDetails$gameId", refresh)
        return@withContext GameDetails(json.getJSONObject(0))
    }

    override suspend fun getGamesByPlatform(platformId : String, refresh: Boolean, pageSize: Int, pageIndex: Int) : List<GameDetails> = withContext(Dispatchers.IO) {

        val apicalypse = APICalypse().fields("name, id, cover.url")
            .where("cover != n & total_rating_count >= 10 & total_rating != 0 & aggregated_rating != 0 & platforms = [$platformId]")
            .sort("rating", Sort.DESCENDING)
            .limit(pageSize)
            .offset(pageIndex * pageSize)
        val json = makeRequest ({ IGDBWrapper.jsonGames(apicalypse) }, "getGamesByPlatform$platformId", refresh)
        return@withContext Converter.fromJsonArrayToGameDetailsArrayList(json)
    }

    override suspend fun getGamesByIds(gameIds: List<String>, refresh: Boolean): List<GameDetails> = withContext(Dispatchers.IO) {
        if (gameIds.isEmpty())
            return@withContext arrayListOf()
        val idListString = buildIdsForRequest(gameIds)
        val apicalypse = APICalypse()
            .fields("name, id, cover.url, genres.name, total_rating, platforms.name, first_release_date")
            .where("id = $idListString")
        val json = makeRequest ({ IGDBWrapper.jsonGames(apicalypse) }, "getGamesByIds${idListString}", refresh)
        return@withContext Converter.fromJsonArrayToGameDetailsArrayList(json)
    }

    override suspend fun getGamesByType(type: GameTypeEnum, refresh: Boolean, pageSize: Int, pageIndex: Int): List<GameDetails> = withContext(Dispatchers.IO) {
        return@withContext when (type) {
            GameTypeEnum.MOST_HYPED -> getGamesMostHyped(refresh, pageSize, pageIndex)
            GameTypeEnum.MOST_RATED -> getGamesMostRated(refresh, pageSize, pageIndex)
            GameTypeEnum.UPCOMING -> getUpcomingGames(refresh, pageSize, pageIndex)
            GameTypeEnum.RECENTLY_RELEASED -> getRecentlyReleasedGames(refresh, pageSize, pageIndex)
            GameTypeEnum.MOST_POPULAR -> getMostPopularGames(refresh, pageSize, pageIndex)
            GameTypeEnum.WORST_RATED -> getWorstRatedGames(refresh, pageSize, pageIndex)
            GameTypeEnum.LOVED_BY_CRITICS -> getLovedByCriticsGames(refresh, pageSize, pageIndex)
            GameTypeEnum.BEST_RATED -> getBestRatedGames(refresh, pageSize, pageIndex)
        }
    }

    private suspend fun getGamesMostRated(refresh: Boolean, pageSize: Int, pageIndex: Int): List<GameDetails> {
        val apicalypse = APICalypse().fields("name, id, cover.url")
            .where("total_rating != 0 & aggregated_rating != 0 & aggregated_rating_count >=10")
            .sort("total_rating_count", Sort.DESCENDING)
            .limit(pageSize)
            .offset(pageIndex * pageSize)
        val json = runBlocking { makeRequest ({ IGDBWrapper.jsonGames(apicalypse) }, "getGamesMostRated${pageIndex}", refresh) }
        return Converter.fromJsonArrayToGameDetailsArrayList(json)
    }

    private suspend fun getLovedByCriticsGames(refresh: Boolean, pageSize: Int, pageIndex: Int): List<GameDetails> {
        val apicalypse = APICalypse().fields("name, id, cover.url")
            .where("total_rating_count >= 10 & total_rating != 0 & aggregated_rating_count >=10")
            .sort("aggregated_rating", Sort.DESCENDING)
            .limit(pageSize)
            .offset(pageIndex * pageSize)
        val json = runBlocking { makeRequest ({ IGDBWrapper.jsonGames(apicalypse) }, "getLovedByCriticsGames${pageIndex}", refresh) }
        return Converter.fromJsonArrayToGameDetailsArrayList(json)
    }

    private suspend fun getGamesMostHyped(refresh: Boolean, pageSize: Int, pageIndex: Int): List<GameDetails> = withContext(Dispatchers.IO) {
        val apicalypse = APICalypse().fields("name, id, cover.url")
            .where("cover != n & hypes != 0 & first_release_date > " + (System.currentTimeMillis() / 1000L))
            .sort("hypes", Sort.DESCENDING)
            .limit(pageSize)
            .offset(pageIndex * pageSize)
        val json = makeRequest ({ IGDBWrapper.jsonGames(apicalypse) }, "getGamesMostHyped${pageIndex}", refresh)
        return@withContext Converter.fromJsonArrayToGameDetailsArrayList(json)
    }

    private suspend fun getBestRatedGames(refresh: Boolean, pageSize: Int, pageIndex: Int): List<GameDetails> = withContext(Dispatchers.IO) {
        val apicalypse = APICalypse().fields("name, id, cover.url")
            .where("cover != n & total_rating_count >= 10 &  aggregated_rating_count >= 10")
            .sort("total_rating", Sort.DESCENDING)
            .limit(pageSize)
            .offset(pageIndex * pageSize)
        val json = makeRequest ({ IGDBWrapper.jsonGames(apicalypse) }, "getBestRatedGames${pageIndex}", refresh)
        return@withContext Converter.fromJsonArrayToGameDetailsArrayList(json)
    }

    private suspend fun getWorstRatedGames(refresh: Boolean, pageSize: Int, pageIndex: Int): List<GameDetails> = withContext(Dispatchers.IO) {
        val apicalypse = APICalypse().fields("name, id, cover.url")
            .where("cover != n & total_rating_count >= 10")
            .sort("total_rating", Sort.ASCENDING)
            .limit(pageSize)
            .offset(pageIndex * pageSize)
        val json = makeRequest ({ IGDBWrapper.jsonGames(apicalypse) }, "getWorstRatedGames${pageIndex}", refresh)
        return@withContext Converter.fromJsonArrayToGameDetailsArrayList(json)
    }

    private suspend fun getMostPopularGames(refresh: Boolean, pageSize: Int, pageIndex: Int): List<GameDetails> = withContext(Dispatchers.IO) {
        val apicalypse = APICalypse()
            .fields("name, id, cover.url")
            .where("first_release_date > " + ((System.currentTimeMillis() / 1000) - 6 * secondsInAWeek)
                        + " & hypes != 0 & follows != 0 & total_rating_count != 0")
            .sort("total_rating", Sort.DESCENDING)
            .limit(pageSize)
            .offset(pageIndex * pageSize)
        val json = makeRequest ({ IGDBWrapper.jsonGames(apicalypse) }, "getMostPopularGames${pageIndex}", refresh)
        return@withContext Converter.fromJsonArrayToGameDetailsArrayList(json)
    }

    private suspend fun getRecentlyReleasedGames(refresh: Boolean, pageSize: Int, pageIndex: Int): List<GameDetails> = withContext(Dispatchers.IO) {
        val apicalypse = APICalypse()
            .fields("name, id, cover.url")
            .where("first_release_date < " + (System.currentTimeMillis() / 1000L))
            .sort("first_release_date", Sort.DESCENDING)
            .limit(pageSize)
            .offset(pageIndex * pageSize)
        val json = makeRequest ({ IGDBWrapper.jsonGames(apicalypse) }, "getRecentlyReleasedGames${pageIndex}", refresh)
        return@withContext Converter.fromJsonArrayToGameDetailsArrayList(json)
    }

    private suspend fun getUpcomingGames(refresh: Boolean, pageSize: Int, pageIndex: Int): List<GameDetails> = withContext(Dispatchers.IO) {
        val apicalypse = APICalypse()
            .fields("name, id, cover.url")
            .where("first_release_date > " + (System.currentTimeMillis() / 1000))
            .sort("total_rating_count", Sort.ASCENDING)
            .limit(pageSize)
            .offset(pageIndex * pageSize)
        val json = makeRequest ({ IGDBWrapper.jsonGames(apicalypse) }, "getUpcomingGames${pageIndex}", refresh)
        return@withContext Converter.fromJsonArrayToGameDetailsArrayList(json)
    }

    private fun buildIdsForRequest(ids : List<Any>) : String {
        return ids.toString().replace("[", "(").replace("]", ")");
    }

}