package com.cip.cipstudio.dataSource.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cip.cipstudio.dataSource.database.dao.GameViewedHistoryDao
import com.cip.cipstudio.model.entity.GameViewedHistoryEntry

@Database(entities = [GameViewedHistoryEntry::class], version = 1, exportSchema = true)
abstract class GameViewedHistoryRoomDatabase : RoomDatabase() {
    abstract fun gameViewedHistoryDao(): GameViewedHistoryDao

    companion object {

        @Volatile
        private var INSTANCE: GameViewedHistoryRoomDatabase? = null

        fun getDatabase(context: Context): GameViewedHistoryRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameViewedHistoryRoomDatabase::class.java,
                    "game_viewed_history_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}