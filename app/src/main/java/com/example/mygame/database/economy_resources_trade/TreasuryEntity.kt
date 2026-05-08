package com.example.mygame.database.economy_resources_trade

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "treasury",
    indices = [Index(value = ["ownerType", "ownerRef"], unique = true, name = "idx_treasury_owner")]
)
data class TreasuryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ownerType: String,
    val ownerRef: Long,
    val silverCoins: Int,
    val goldCoins: Int
)