package com.cip.cipstudio.dataSource.repository.historyRepository

import com.cip.cipstudio.model.entity.GameViewedHistoryEntry

interface HistoryRepository {
    suspend fun insert(id: String, userId: String)
    suspend fun deleteAll(userId: String)
    suspend fun getAllHistory(userId: String) : List<String>
    suspend fun getHistory(userId: String, pageSize: Int = 10, pageIndex: Int = 0) : List<String>
    suspend fun syncHistory(list : List<GameViewedHistoryEntry>)
}
