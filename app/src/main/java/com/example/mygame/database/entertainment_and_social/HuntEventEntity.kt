package com.example.mygame.database.entertainment_and_social

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hunt_event")
data class HuntEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val turn: Int
)