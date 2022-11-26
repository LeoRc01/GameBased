package com.cip.cipstudio.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_viewed_history")
data class GameViewedHistoryEntry(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "last_view") val lastVisited: Long = System.currentTimeMillis() ){

}