package com.example.mygame.database.armies_units_warfare

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "casualty")
data class CasualtyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val battleId: Long,
    val unitId: Long,
    val count: Int
)