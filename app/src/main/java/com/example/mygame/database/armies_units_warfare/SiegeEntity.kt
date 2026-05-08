package com.example.mygame.database.armies_units_warfare

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "siege")
data class SiegeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val attackerArmyId: Long,
    val defenderArmyId: Long?,
    val startedTurn: Int
)