package com.example.mygame.database.world_and_geography

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "build_order",
    indices = [Index(value = ["landId"], name = "idx_build_order_land")]
)
data class BuildOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val structureType: String,
    val assignedBuilders: Int
)