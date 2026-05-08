package com.example.mygame.database.world_and_geography

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "land",
    indices = [Index(value = ["countryId"], name = "idx_land_country")]
)
data class LandEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val countryId: Long,
    val terrain: String
)