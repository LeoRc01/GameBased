package com.cip.cipstudio.dataSource.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cip.cipstudio.model.entity.GameViewedHistoryEntry

@Dao
interface GameViewedHistoryDao {
    @Query("SELECT * FROM game_viewed_history ORDER BY last_view DESC")
    suspend fun getAllOrderedByTime(): List<GameViewedHistoryEntry>

    @Query("SELECT * FROM game_viewed_history ORDER BY last_view DESC LIMIT 10")
    suspend fun getFirstTenOrderedByTime(): List<GameViewedHistoryEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gameViewedHistoryEntry: GameViewedHistoryEntry)

    @Query("DELETE FROM game_viewed_history")
    suspend fun deleteAll()
}