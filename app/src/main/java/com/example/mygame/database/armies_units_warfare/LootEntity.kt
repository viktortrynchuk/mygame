package com.example.mygame.database.armies_units_warfare

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loot")
data class LootEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val battleId: Long,
    val itemId: String,
    val qty: Int
)