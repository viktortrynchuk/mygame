package com.example.mygame.database.economy_resources_trade

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tax_policy",
    indices = [Index(value = ["landId"], unique = true, name = "idx_tax_policy_land")]
)
data class TaxPolicyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val rate: Int
)