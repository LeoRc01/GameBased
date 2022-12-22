package com.cip.cipstudio.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "game_viewed_history", primaryKeys = ["user_id", "game_id"])
data class GameViewedHistoryEntry(
    @ColumnInfo(name = "game_id") val gameId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "last_view") val lastVisited: Long = System.currentTimeMillis() ){

}