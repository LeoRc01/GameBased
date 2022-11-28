package com.cip.cipstudio.dataSource.repository

import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.model.data.PlatformDetails
import com.cip.cipstudio.utils.GameTypeEnum


interface IGDBRepository {

    suspend fun getGamesByType(type : GameTypeEnum, refresh: Boolean = false, pageSize: Int = 10, pageIndex: Int = 0): List<GameDetails>

    suspend fun getGameDetails(gameId : String, refresh: Boolean = false) : GameDetails

    suspend fun getGamesByIds(gameIds : List<String>, refresh: Boolean = false) : List<GameDetails>

    suspend fun getPlatformsInfo(platformIds : List<String>, refresh: Boolean = false) : List<PlatformDetails>

    suspend fun getGamesByPlatform(platformId : String, refresh: Boolean = false, pageSize: Int = 10, pageIndex: Int = 0) : List<GameDetails>
    
}