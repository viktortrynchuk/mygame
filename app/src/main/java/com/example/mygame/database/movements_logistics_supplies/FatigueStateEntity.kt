package com.example.mygame.database.movements_logistics_supplies

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fatigue_state")
data class FatigueStateEntity(
    @PrimaryKey val subjectId: Long,
    val level: String,
    val lastRestTurn: Int
)