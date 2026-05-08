package com.example.mygame.database.armies_units_warfare

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flag")
data class FlagEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val unitId: Long,
    val name: String,
    val fearAura: Int
)