package com.example.mygame.database.armies_units_warfare

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "battle_turn")
data class BattleTurnEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val battleId: Long,
    val turn: Int
)