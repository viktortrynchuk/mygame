package com.example.mygame.database.justice_and_court

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trial")
data class TrialEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val crimeId: Long,
    val judgeNobleId: Long?,
    val startedTurn: Int
)