package com.example.mygame.database.rebellion_banditry

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "revolt")
data class RevoltEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goal: String,
    val startedLandId: Long
)