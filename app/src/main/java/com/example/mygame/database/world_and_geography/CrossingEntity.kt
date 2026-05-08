package com.example.mygame.database.world_and_geography

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "crossing",
    indices = [Index(value = ["landId"], name = "idx_crossing_land")]
)
data class CrossingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val type: String,
    val capacity: Int
)