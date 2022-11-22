package com.cip.cipstudio.repository

import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.model.data.PlatformDetails
import com.cip.cipstudio.utils.GameTypeEnum


interface IGDBRepository {

    suspend fun getGamesByType(type : GameTypeEnum, refresh: Boolean = false): List<GameDetails>

    suspend fun getGameDetails(gameId : String, refresh: Boolean = false) : GameDetails

    suspend fun getGamesByIds(gameIds : ArrayList<String>, refresh: Boolean = false) : List<GameDetails>

    suspend fun getPlatformsInfo(platformIds : List<String>, refresh: Boolean = false) : List<PlatformDetails>

    suspend fun getGamesByPlatform(platformId : String, refresh: Boolean = false) : List<GameDetails>
    
}