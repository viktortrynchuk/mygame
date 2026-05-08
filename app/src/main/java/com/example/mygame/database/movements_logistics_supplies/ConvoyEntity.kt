package com.example.mygame.database.movements_logistics_supplies

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "convoy")
data class ConvoyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val armyId: Long?,
    val landId: Long
)