package com.example.mygame.database.foundations_core

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "turn_clock")
data class TurnClockEntity(
    @PrimaryKey @ColumnInfo(defaultValue = "1") val id: Int = 1,
    val turn: Int,
    val isNight: Boolean,
    val season: String,
    val seed: Long
)