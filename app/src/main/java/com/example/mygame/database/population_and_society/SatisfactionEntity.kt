package com.example.mygame.database.population_and_society

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "satisfaction")
data class SatisfactionEntity(
    @PrimaryKey val landId: Long,
    val level: Int
)