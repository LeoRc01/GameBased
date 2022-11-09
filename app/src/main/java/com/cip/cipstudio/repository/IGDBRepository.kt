package com.cip.cipstudio.repository

import com.cip.cipstudio.model.data.GameDetails


interface IGDBRepository {

    suspend fun getGamesMostHyped() : List<GameDetails>

    suspend fun getGamesMostRated() : List<GameDetails>

    suspend fun getGamesDetails(gameId : String) : GameDetails

    suspend fun getGamesByIds(gameIds : ArrayList<String>) : List<GameDetails>
    
}