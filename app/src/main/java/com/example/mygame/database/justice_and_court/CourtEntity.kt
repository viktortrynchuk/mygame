package com.example.mygame.database.justice_and_court

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "court")
data class CourtEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val landId: Long
)