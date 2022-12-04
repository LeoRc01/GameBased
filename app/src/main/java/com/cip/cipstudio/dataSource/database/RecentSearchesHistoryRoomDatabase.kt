package com.cip.cipstudio.dataSource.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cip.cipstudio.dataSource.database.dao.RecentSearchesHistoryDao
import com.cip.cipstudio.model.entity.RecentSearchesHistoryEntry

@Database(entities = [RecentSearchesHistoryEntry::class], version = 1, exportSchema = true)
abstract class RecentSearchesHistoryRoomDatabase : RoomDatabase() {
    abstract fun recentSearchesHistoryDao(): RecentSearchesHistoryDao

    companion object {

        @Volatile
        private var INSTANCE: RecentSearchesHistoryRoomDatabase? = null

        fun getDatabase(context: Context): RecentSearchesHistoryRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecentSearchesHistoryRoomDatabase::class.java,
                    "recent_searches_history_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}