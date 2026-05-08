package com.example.mygame.database.armies_units_warfare

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "battle_participant",
    indices = [Index(value = ["battleId"], name = "idx_battle_participant_battle")]
)
data class BattleParticipantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val battleId: Long,
    val armyId: Long,
    val side: String
)