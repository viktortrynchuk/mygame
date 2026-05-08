package com.example.mygame.database.armies_units_warfare

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "unit_composition")
data class UnitCompositionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val unitId: Long,
    val soldierType: String,
    val count: Int
)