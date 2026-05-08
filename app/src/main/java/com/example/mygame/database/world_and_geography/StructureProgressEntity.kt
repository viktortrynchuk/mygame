package com.example.mygame.database.world_and_geography

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "structure_progress")
data class StructureProgressEntity(
    @PrimaryKey val structureId: Long,
    val totalEffort: Int,
    val doneEffort: Int
)