package com.cip.cipstudio.dataSource.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cip.cipstudio.model.entity.RecentSearchesHistoryEntry

private val TAG = "RecentSearchesHistoryDao"
@Dao
interface RecentSearchesHistoryDao {

    @Query("SELECT * FROM recent_searched_history WHERE user_id =:userId  ORDER BY last_search DESC")
    suspend fun getAllOrderedByTime(userId : String): List<RecentSearchesHistoryEntry>

    @Query("SELECT * FROM recent_searched_history WHERE INSTR(id, :query) > 0 AND user_id =:userId ORDER BY last_search DESC LIMIT :pageSize OFFSET :pageIndex * :pageSize")
    suspend fun getOrderedByTime(query: String, userId : String, pageSize: Int, pageIndex : Int = 0): List<RecentSearchesHistoryEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recentSearchesHistoryEntry: RecentSearchesHistoryEntry)

    @Query("DELETE FROM recent_searched_history WHERE id =:id")
    suspend fun delete(id : String)

    @Query("DELETE FROM recent_searched_history WHERE user_id =:userId")
    suspend fun deleteAll(userId : String)

    @Query("DELETE FROM recent_searched_history")
    suspend fun deleteAllTable()
}