package com.example.mygame.database.justice_and_court

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "court_case")
data class CaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courtId: Long,
    val accusedId: Long,
    val crime: String,
    val turnOpened: Int
)