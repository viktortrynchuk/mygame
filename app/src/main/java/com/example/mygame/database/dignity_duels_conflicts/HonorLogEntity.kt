package com.example.mygame.database.dignity_duels_conflicts

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "honor_log",
    indices = [Index(value = ["nobleId"], name = "idx_honor_log_noble")]
)
data class HonorLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nobleId: Long,
    val delta: Int,
    val reason: String,
    val turn: Int
)