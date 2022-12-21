package com.cip.cipstudio.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_searched_history", primaryKeys = ["query", "user_id"])
data class RecentSearchesHistoryEntry(
    @ColumnInfo(name = "query") val query: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "last_search") val lastSearched: Long = System.currentTimeMillis() ){

}