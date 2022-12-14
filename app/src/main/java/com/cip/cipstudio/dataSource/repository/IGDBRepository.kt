package com.cip.cipstudio.dataSource.repository

import com.cip.cipstudio.dataSource.filter.criteria.*
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.model.data.PlatformDetails
import com.cip.cipstudio.utils.GameTypeEnum
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


interface IGDBRepository {

    suspend fun getGamesByType(type : GameTypeEnum,
                               refresh: Boolean = false,
                               pageSize: Int = 10,
                               pageIndex: Int = 0,
                               filterCriteria: Criteria = OperatorCriteria(OperatorEnum.AND)
                            ): List<GameDetails>

    suspend fun getGameDetails(gameId : String, refresh: Boolean = false) : GameDetails

    suspend fun getGamesByIds(gameIds : List<String>, refresh: Boolean = false) : List<GameDetails>

    suspend fun getPlatformsInfo(platformIds : List<String>, refresh: Boolean = false) : List<PlatformDetails>

    suspend fun getGamesByPlatform(platformId : String, refresh: Boolean = false, pageSize: Int = 10, pageIndex: Int = 0) : List<GameDetails>

    suspend fun searchGames(searchText: String,
                            pageIndex: Int = 0,
                            pageSize: Int = 10,
                            refresh: Boolean = false,
                            filterCriteria: Criteria = OperatorCriteria(OperatorEnum.AND),
                            sortCriteria: SortCriteria = SortCriteria(SortEnum.Default)
                        ): List<GameDetails>

    suspend fun getSearchSuggestions(searchText: String, limit: Int = 3, refresh: Boolean = false): List<GameDetails>

    suspend fun getGamesByCollectionName(collectionName : String, refresh: Boolean = false): List<GameDetails>

    suspend fun getGenres() : ArrayList<JSONObject>

    suspend fun getPlayerPerspectives() : ArrayList<JSONObject>

    suspend fun getThemes() : ArrayList<JSONObject>

    suspend fun getGameModes() : ArrayList<JSONObject>

    suspend fun getFirstAndLastYearsOfRelease(): List<Float>

    suspend fun getPlatforms(offset : Int, exclude: List<String> = arrayListOf()) : List<PlatformDetails>

}