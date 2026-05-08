package com.example.mygame.database.population_and_society

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "population_stat")
data class PopulationStatEntity(
    @PrimaryKey val landId: Long,
    val total: Int
)