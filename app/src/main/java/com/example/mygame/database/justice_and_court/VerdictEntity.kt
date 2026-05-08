package com.example.mygame.database.justice_and_court

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "verdict")
data class VerdictEntity(
    @PrimaryKey(autoGenerate = true) val Id: Long = 0,
    val caseId: Long,
    val guilty: Boolean,
    val turn: Int
)