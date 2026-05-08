package com.example.mygame.database.economy_resources_trade

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "merchant_assignment")
data class MerchantAssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val merchantId: Long,
    val routeId: Long
)