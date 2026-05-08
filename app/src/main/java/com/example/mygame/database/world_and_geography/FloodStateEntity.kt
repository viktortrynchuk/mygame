package com.example.mygame.database.world_and_geography

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flood_state")
data class FloodStateEntity(
    @PrimaryKey val landId: Long,
    val floodTurns: Int
)