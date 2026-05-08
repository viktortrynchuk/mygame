package com.example.mygame.database.rebellion_banditry

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spread_event")
data class SpreadEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val revoltId: Long,
    val landId: Long,
    val turn: Int
)