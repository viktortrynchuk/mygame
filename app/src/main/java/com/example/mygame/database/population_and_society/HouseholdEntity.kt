package com.example.mygame.database.population_and_society

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "household",
    indices = [Index(value = ["landId"], name = "idx_household_land")]
)
data class HouseholdEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val type: String,
    val tier: String
)