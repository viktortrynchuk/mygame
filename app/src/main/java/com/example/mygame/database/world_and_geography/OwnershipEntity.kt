package com.example.mygame.database.world_and_geography

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ownership")
data class OwnershipEntity(
    @PrimaryKey val landId: Long,
    val ownerType: String,
    val ownerRef: Long
)