package com.example.mygame.database.armies_units_warfare

import androidx.room.Entity

@Entity(
    tableName = "equipment_stock",
    primaryKeys = ["ownerType", "ownerRef", "itemId"]
)
data class EquipmentStockEntity(
    val ownerType: String,
    val ownerRef: Long,
    val itemId: String,
    val qty: Int
)