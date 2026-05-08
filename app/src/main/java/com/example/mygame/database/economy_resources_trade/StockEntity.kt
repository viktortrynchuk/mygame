package com.example.mygame.database.economy_resources_trade

import androidx.room.Entity

@Entity(
    tableName = "stock",
    primaryKeys = ["warehouseId", "itemId"]
)
data class StockEntity(
    val warehouseId: Long,
    val itemId: String,
    val qty: Int,
    val volume: Int
)