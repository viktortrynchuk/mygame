package com.example.mygame.database.economy_resources_trade

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fishery")
data class FisheryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long
)