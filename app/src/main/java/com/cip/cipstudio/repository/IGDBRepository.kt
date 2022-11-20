package com.cip.cipstudio.repository

import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.utils.GameTypeEnum


interface IGDBRepository {

    suspend fun getGamesByType(type : GameTypeEnum): List<GameDetails>

    suspend fun getGameDetails(gameId : String) : GameDetails

    suspend fun getGamesByIds(gameIds : ArrayList<String>) : List<GameDetails>
    
}