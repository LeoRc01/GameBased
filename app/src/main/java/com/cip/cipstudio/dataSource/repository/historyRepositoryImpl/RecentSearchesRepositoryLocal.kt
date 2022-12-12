package com.cip.cipstudio.dataSource.repository.historyRepositoryImpl

import android.content.Context
import com.cip.cipstudio.dataSource.database.RecentSearchesHistoryRoomDatabase
import com.cip.cipstudio.dataSource.repository.RecentSearchesRepository
import com.cip.cipstudio.model.entity.RecentSearchesHistoryEntry

class RecentSearchesRepositoryLocal(context: Context) : RecentSearchesRepository {

    private val localDB =
        RecentSearchesHistoryRoomDatabase.getDatabase(context).recentSearchesHistoryDao()
    private val TAG = "HistoryRepositoryLocal"

    override suspend fun insert(id: String, userId: String) {
        val gameViewedHistoryEntry = RecentSearchesHistoryEntry(id, userId)
        localDB.insert(gameViewedHistoryEntry)
    }

    override suspend fun deleteAll(userId: String) {
        localDB.deleteAll(userId)
    }

    override suspend fun getAllRecentSearches(userId: String): List<String> {
        return localDB.getAllOrderedByTime(userId).map { it.id }
    }

    override suspend fun getRecentSearches(query: String, userId: String, pageSize: Int, pageIndex: Int): List<String> {
        return localDB.getOrderedByTime(query, userId, pageSize, pageIndex).map { it.id }
    }

    override suspend fun syncRecentSearches(list: List<RecentSearchesHistoryEntry>) {
        list.forEach {
            localDB.insert(it)
        }
    }

    override suspend fun delete(id: String) {
        localDB.delete(id)
    }
}