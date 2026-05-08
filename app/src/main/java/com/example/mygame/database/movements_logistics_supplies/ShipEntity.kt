package com.example.mygame.database.movements_logistics_supplies

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ship")
data class ShipEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val portLandId: Long,
    val capacity: Int
)