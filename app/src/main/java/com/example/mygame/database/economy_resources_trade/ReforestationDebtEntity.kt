package com.example.mygame.database.economy_resources_trade

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reforestation_debt")
data class ReforestationDebtEntity(
    @PrimaryKey val landId: Long,
    val debt: Int
)