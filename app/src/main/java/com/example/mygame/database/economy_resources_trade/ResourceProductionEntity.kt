package com.example.mygame.database.economy_resources_trade

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "resource_production")
data class ResourceProductionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val itemId: String,
    val effortPerTurn: Int
)