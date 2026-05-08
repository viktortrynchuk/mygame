package com.example.mygame.database.population_and_society

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mayor")
data class MayorEntity(
    @PrimaryKey val landId: Long,
    val name: String,
    val actorId: Long
)