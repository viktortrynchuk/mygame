package com.example.mygame.database.dignity_duels_conflicts

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personal_conflict")
data class PersonalConflictEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nobleA: Long,
    val nobleB: Long,
    val startedTurn: Int,
    val state: String
)