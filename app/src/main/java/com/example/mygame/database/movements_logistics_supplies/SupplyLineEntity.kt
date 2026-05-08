package com.example.mygame.database.movements_logistics_supplies

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "supply_line")
data class SupplyLineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val armyId: Long,
    val sourceLandId: Long,
    val active: Boolean
)