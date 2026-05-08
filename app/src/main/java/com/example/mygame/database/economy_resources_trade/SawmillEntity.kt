package com.example.mygame.database.economy_resources_trade

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sawmill")
data class SawmillEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long
)