package com.example.mygame.database.dignity_duels_conflicts

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "duel")
data class DuelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val participantA: Long,
    val participantB: Long,
    val turn: Int,
    val outcome: String
)