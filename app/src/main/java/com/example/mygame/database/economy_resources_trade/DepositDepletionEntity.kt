package com.example.mygame.database.economy_resources_trade

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deposit_depletion")
data class DepositDepletionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mineId: Long,
    val turn: Int,
    val delta: Int
)