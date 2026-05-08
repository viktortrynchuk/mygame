package com.example.mygame.database.movements_logistics_supplies

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "logistics_log")
data class LogisticsLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val turn: Int,
    val armyId: Long?,
    val details: String
)