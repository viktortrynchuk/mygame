package com.example.mygame.database.justice_and_court

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "crime")
data class CrimeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val type: String,
    val reportedBy: Long?,
    val details: String?,
    val turn: Int
)