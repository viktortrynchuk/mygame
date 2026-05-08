package com.example.mygame.database.world_and_geography

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fortification",
    indices = [Index(value = ["landId"], name = "idx_fortification_land")]
)
data class FortificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val level: Int,
    val breached: Boolean
)