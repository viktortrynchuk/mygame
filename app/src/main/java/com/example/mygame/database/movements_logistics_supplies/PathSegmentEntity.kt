package com.example.mygame.database.movements_logistics_supplies

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "path_segment",
    indices = [Index(value = ["movementOrderId"], name = "idx_path_segment_order")]
)
data class PathSegmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val movementOrderId: Long,
    val stepIndex: Int,
    val fromLandId: Long,
    val toLandId: Long
)