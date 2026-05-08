package com.example.mygame.database.rebellion_banditry

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rebellion")
data class RebellionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val cause: String,
    val startedTurn: Int
)