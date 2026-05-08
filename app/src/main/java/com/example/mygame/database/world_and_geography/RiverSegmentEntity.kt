package com.example.mygame.database.world_and_geography

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "river_segment",
    primaryKeys = ["riverId", "orderIndex"],
    indices = [Index(value = ["riverId"], name = "idx_river_segment_river")]
)
data class RiverSegmentEntity(
    val riverId: Long,
    val orderIndex: Int,
    val landId: Long
)