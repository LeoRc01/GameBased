package com.cip.cipstudio.repository

import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject
import proto.Game


interface IGDBRepository {

    suspend fun getGamesMostHyped() : JSONArray

    suspend fun getGamesMostRated() : JSONArray
    
}