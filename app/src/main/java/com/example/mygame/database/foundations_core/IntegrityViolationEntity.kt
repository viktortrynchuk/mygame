package com.example.mygame.database.foundations_core

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "integrity_violation",
    indices = [Index(value = ["turn"], name = "idx_integrity_violation_turn")]
)
data class IntegrityViolationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val turn: Int,
    val code: String,
    val details: String,
    val createdAt: Long
)