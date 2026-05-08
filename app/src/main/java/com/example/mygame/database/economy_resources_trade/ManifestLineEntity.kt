package com.example.mygame.database.economy_resources_trade

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "manifest_line")
data class ManifestLineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routeId: Long,
    val marketId: Long,
    val itemId: String,
    val action: String,
    val qty: Int
)