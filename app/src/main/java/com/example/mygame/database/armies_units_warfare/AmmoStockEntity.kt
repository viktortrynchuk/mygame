package com.example.mygame.database.armies_units_warfare

import androidx.room.Entity

@Entity(
    tableName = "ammo_stock",
    primaryKeys = ["ownerType", "ownerRef", "ammoType"]
)
data class AmmoStockEntity(
    val ownerType: String,
    val ownerRef: Long,
    val ammoType: String,
    val qty: Int
)