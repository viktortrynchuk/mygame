package com.example.mygame.database.world_and_geography

import androidx.room.Entity

@Entity(
    tableName = "neighbor",
    primaryKeys = ["landId", "neighborId"]
)
data class NeighborEntity(
    val landId: Long,
    val neighborId: Long
)