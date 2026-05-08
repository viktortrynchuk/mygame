package com.example.mygame.database.economy_resources_trade

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "resource_stock",
    indices = [Index(value = ["landId"], name = "idx_resource_stock_land")]
)
data class ResourceStockEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val itemId: String,
    val qty: Int
)