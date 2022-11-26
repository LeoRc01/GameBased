package com.cip.cipstudio.dataSource.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cip.cipstudio.model.entity.GameViewedHistoryEntry

@Dao
interface GameViewedHistoryDao {
    @Query("SELECT * FROM game_viewed_history WHERE user_id =:userId  ORDER BY last_view DESC")
    suspend fun getAllOrderedByTime(userId : String): List<GameViewedHistoryEntry>

    @Query("SELECT * FROM game_viewed_history WHERE user_id =:userId ORDER BY last_view DESC LIMIT 10")
    suspend fun getFirstTenOrderedByTime(userId : String): List<GameViewedHistoryEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gameViewedHistoryEntry: GameViewedHistoryEntry)

    @Query("DELETE FROM game_viewed_history WHERE user_id =:userId")
    suspend fun deleteAll(userId : String)

    @Query("DELETE FROM game_viewed_history")
    suspend fun deleteAllTable()
}