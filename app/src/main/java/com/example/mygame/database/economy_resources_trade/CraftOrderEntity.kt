package com.example.mygame.database.economy_resources_trade

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "craft_order")
data class CraftOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val recipeId: Long,
    val assignedWorkers: Int
)