package com.example.mygame.database.economy_resources_trade

import androidx.room.Entity

@Entity(
    tableName = "market_stock",
    primaryKeys = ["marketId", "itemId"]
)
data class MarketStockEntity(
    val marketId: Long,
    val itemId: String,
    val qty: Int
)