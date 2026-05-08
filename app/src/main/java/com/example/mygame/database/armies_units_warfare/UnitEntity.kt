package com.example.mygame.database.armies_units_warfare

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "unit",
    indices = [Index(value = ["armyId"], name = "idx_unit_army")]
)
data class UnitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val armyId: Long,
    val type: String,
    val mounted: Boolean,
    val armor: String?
)