package com.cip.cipstudio.dataSource.repository

import com.cip.cipstudio.model.entity.GameViewedHistoryEntry

interface HistoryRepository {
    suspend fun insert(id: String, userId: String)
    suspend fun deleteAll(userId: String)
    suspend fun getHistory(userId: String) : List<String>
    suspend fun getFirstTenHistory(userId: String) : List<String>
    suspend fun syncHistory(list : List<GameViewedHistoryEntry>)
}
