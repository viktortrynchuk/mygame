package com.example.mygame.database.movements_logistics_supplies

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movement_order")
data class MovementOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val armyId: Long,
    val carrierType: String = "ARMY",
    val messageId: Long? = null,
    val sourceLandId: Long? = null,
    val targetLandId: Long? = null,
    val createdTurn: Int,
    val status: String
)