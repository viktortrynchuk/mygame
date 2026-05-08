package com.example.mygame.database.dignity_duels_conflicts

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offense")
data class OffenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val offenderId: Long,
    val victimId: Long,
    val turn: Int,
    val details: String
)