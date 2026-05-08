package com.example.mygame.database.rebellion_banditry

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suppression_log")
data class SuppressionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val turn: Int,
    val landId: Long,
    val details: String
)