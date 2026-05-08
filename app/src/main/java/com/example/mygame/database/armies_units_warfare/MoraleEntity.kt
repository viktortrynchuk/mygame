package com.example.mygame.database.armies_units_warfare

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "morale")
data class MoraleEntity(
    @PrimaryKey val unitId: Long,
    val value: Int
)