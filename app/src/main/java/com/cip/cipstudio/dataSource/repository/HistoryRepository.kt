package com.cip.cipstudio.dataSource.repository

import com.cip.cipstudio.model.entity.GameViewedHistoryEntry

interface HistoryRepository {
    suspend fun insert(id: String)
    suspend fun deleteAll()
    suspend fun getHistory() : List<String>
    suspend fun getFirstTenHistory() : List<String>
    suspend fun syncHistory(list : List<GameViewedHistoryEntry>)
}
