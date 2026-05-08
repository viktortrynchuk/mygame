package com.example.mygame.database.world_and_geography

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_poison_state")
data class WaterPoisonStateEntity(
    @PrimaryKey val landId: Long,
    val poisonedUntilTurn: Int
)