package com.example.mygame.database.world_and_geography

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "terrain")
data class TerrainEntity(
    @PrimaryKey val code: String,
    val moveCost: Int
)