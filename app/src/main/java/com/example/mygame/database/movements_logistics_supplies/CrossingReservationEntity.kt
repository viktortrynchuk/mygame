package com.example.mygame.database.movements_logistics_supplies

import androidx.room.Entity

@Entity(
    tableName = "crossing_reservation",
    primaryKeys = ["turn", "landId", "slotType", "unitId"]
)
data class CrossingReservationEntity(
    val turn: Int,
    val landId: Long,
    val slotType: String,
    val unitId: Long
)