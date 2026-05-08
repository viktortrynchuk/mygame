package com.example.mygame.database.economy_resources_trade

import androidx.room.Entity

@Entity(
    tableName = "price",
    primaryKeys = ["marketId", "itemId"]
)
data class PriceEntity(
    val marketId: Long,
    val itemId: String,
    val price: Int
)