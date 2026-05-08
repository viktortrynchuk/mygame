package com.example.mygame.database.movements_logistics_supplies

import androidx.room.Entity

@Entity(
    tableName = "route_step",
    primaryKeys = ["routeId", "stepIndex"]
)
data class RouteStepEntity(
    val routeId: Long,
    val stepIndex: Int,
    val landId: Long
)