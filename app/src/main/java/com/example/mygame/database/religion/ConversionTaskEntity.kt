package com.example.mygame.database.religion

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "conversion_task",
    indices = [Index(value = ["targetType", "targetRef"], name = "idx_conversion_target")]
)
data class ConversionTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetType: String,
    val targetRef: Long,
    val religionId: Long,
    val progress: Int
)