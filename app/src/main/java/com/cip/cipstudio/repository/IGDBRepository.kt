package com.cip.cipstudio.repository

import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.model.data.PlatformDetails
import com.cip.cipstudio.utils.GameTypeEnum


interface IGDBRepository {

    suspend fun getGamesByType(type : GameTypeEnum): List<GameDetails>

    suspend fun getGameDetails(gameId : String) : GameDetails

    suspend fun getGamesByIds(gameIds : ArrayList<String>) : List<GameDetails>

    suspend fun getPlatformsInfo(platformIds : List<String>) : List<PlatformDetails>

    suspend fun getGamesByPlatform(platformId : String) : List<GameDetails>
    
}