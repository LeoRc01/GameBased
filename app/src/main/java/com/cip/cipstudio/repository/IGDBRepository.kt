package com.cip.cipstudio.repository

import com.cip.cipstudio.model.data.GameDetailsJson
import org.json.JSONArray


interface IGDBRepository {

    suspend fun getGamesMostHyped() : List<GameDetailsJson>

    suspend fun getGamesMostRated() : List<GameDetailsJson>

    suspend fun getGamesDetails(gameId : Int) : GameDetailsJson
    
}