package com.cip.cipstudio.dataSource.repository.historyRepositoryImpl

import android.content.Context
import com.cip.cipstudio.dataSource.database.GameViewedHistoryRoomDatabase
import com.cip.cipstudio.dataSource.repository.HistoryRepository
import com.cip.cipstudio.model.entity.GameViewedHistoryEntry

class HistoryRepositoryLocal(context: Context) : HistoryRepository {

    private val localDB = GameViewedHistoryRoomDatabase.getDatabase(context).gameViewedHistoryDao()

    override suspend fun insert(id: String) {
        val gameViewedHistoryEntry = GameViewedHistoryEntry(id)
        localDB.insert(gameViewedHistoryEntry)
    }

    override suspend fun deleteAll() {
        localDB.deleteAll()
    }

    override suspend fun getHistory(): List<String> {
        return localDB.getAllOrderedByTime().map { it.id }
    }

    override suspend fun getFirstTenHistory(): List<String> {
        return localDB.getFirstTenOrderedByTime().map { it.id }
    }

    override suspend fun syncHistory(list: List<GameViewedHistoryEntry>) {
        list.forEach {
            localDB.insert(it)
        }
    }
}