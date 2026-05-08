package com.example.mygame.database.armies_units_warfare

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "desertion_event")
data class DesertionEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val unitId: Long,
    val turn: Int,
    val count: Int
)