package com.example.mygame.database.armies_units_warfare

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "military_order")
data class MilitaryOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val armyId: Long,
    val type: String,
    val payload: String,
    val issuedTurn: Int
)