package com.cip.cipstudio.dataSource.repository.historyRepository

import android.content.Context
import com.cip.cipstudio.dataSource.database.GameViewedHistoryRoomDatabase
import com.cip.cipstudio.model.entity.GameViewedHistoryEntry

class HistoryRepositoryLocal(context: Context) : HistoryRepository {

    private val localDB = GameViewedHistoryRoomDatabase.getDatabase(context).gameViewedHistoryDao()
    private val TAG = "HistoryRepositoryLocal"

    override suspend fun insert(id: String, userId: String) {
        val gameViewedHistoryEntry = GameViewedHistoryEntry(id, userId)
        localDB.insert(gameViewedHistoryEntry)
    }

    override suspend fun deleteAll(userId: String) {
        localDB.deleteAll(userId)
    }

    override suspend fun getAllHistory(userId: String): List<String> {
        return localDB.getAllOrderedByTime(userId).map { it.gameId }
    }

    override suspend fun getHistory(userId: String, pageSize: Int, pageIndex: Int): List<String> {
        return localDB.getOrderedByTime(userId, pageSize, pageIndex).map { it.gameId }
    }

    override suspend fun syncHistory(list: List<GameViewedHistoryEntry>) {
        list.forEach {
            localDB.insert(it)
        }
    }
}