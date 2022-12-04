package com.cip.cipstudio.dataSource.repository

import com.cip.cipstudio.model.entity.RecentSearchesHistoryEntry

interface RecentSearchesRepository {
    suspend fun insert(id: String, userId: String)
    suspend fun deleteAll(userId: String)
    suspend fun getAllRecentSearches(userId: String) : List<String>
    suspend fun getRecentSearches(query: String = "", userId: String, pageSize: Int = 20, pageIndex: Int = 0) : List<String>
    suspend fun syncRecentSearches(list : List<RecentSearchesHistoryEntry>)
    suspend fun delete(id : String)
}