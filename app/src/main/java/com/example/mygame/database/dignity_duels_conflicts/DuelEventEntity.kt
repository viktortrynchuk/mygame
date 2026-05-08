package com.example.mygame.database.dignity_duels_conflicts

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "duel_event")
data class DuelEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val challengerId: Long,
    val challengedId: Long,
    val turn: Int,
    val outcome: String
)