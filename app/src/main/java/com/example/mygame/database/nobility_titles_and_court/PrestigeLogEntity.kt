package com.example.mygame.database.nobility_titles_and_court

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "prestige_log",
    indices = [Index(value = ["nobleId"], name = "idx_prestige_log_noble")]
)
data class PrestigeLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nobleId: Long,
    val delta: Int,
    val reason: String,
    val turn: Int
)