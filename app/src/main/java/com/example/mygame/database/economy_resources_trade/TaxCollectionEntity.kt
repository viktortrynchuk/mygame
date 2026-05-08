package com.example.mygame.database.economy_resources_trade

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tax_collection")
data class TaxCollectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val turn: Int,
    val silverCollected: Int,
    val goldCollected: Int
)