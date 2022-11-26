package com.cip.cipstudio.dataSource.repository.historyRepositoryImpl

import android.content.Context
import com.cip.cipstudio.dataSource.database.GameViewedHistoryRoomDatabase
import com.cip.cipstudio.dataSource.repository.HistoryRepository
import com.cip.cipstudio.model.entity.GameViewedHistoryEntry

class HistoryRepositoryLocal(context: Context) : HistoryRepository {

    private val localDB = GameViewedHistoryRoomDatabase.getDatabase(context).gameViewedHistoryDao()

    override suspend fun insert(id: String, userId: String) {
        val gameViewedHistoryEntry = GameViewedHistoryEntry(id, userId)
        localDB.insert(gameViewedHistoryEntry)
    }

    override suspend fun deleteAll(userId: String) {
        localDB.deleteAll(userId)
    }

    override suspend fun getHistory(userId: String): List<String> {
        return localDB.getAllOrderedByTime(userId).map { it.id }
    }

    override suspend fun getFirstTenHistory(userId: String): List<String> {
        return localDB.getFirstTenOrderedByTime(userId).map { it.id }
    }

    override suspend fun syncHistory(list: List<GameViewedHistoryEntry>) {
        list.forEach {
            localDB.insert(it)
        }
    }
}