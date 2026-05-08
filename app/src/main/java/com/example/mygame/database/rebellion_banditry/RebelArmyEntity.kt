package com.example.mygame.database.rebellion_banditry

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Bridge linking a rebellion to an actual ArmyEntity.
 * Location is read from army.landId (no need for a separate land column here).
 */
@Entity(
    tableName = "rebel_army",
    indices = [
        Index("rebellionId"),
        Index("armyId")
    ]
)
data class RebelArmyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rebellionId: Long,
    val armyId: Long,
    val role: String = "MAIN",     // MAIN | CELL | GARRISON (free-form)
    val createdTurn: Int,
    val disbandedTurn: Int? = null
)