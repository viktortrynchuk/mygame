package com.example.mygame.database.population_and_society

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "work_assignment",
    indices = [Index(value = ["landId"], name = "idx_work_assignment_land")]
)
data class WorkAssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actorId: Long?,
    val landId: Long,
    val profession: String,
    val toolsOk: Boolean,
    val effortPerTurn: Int
)