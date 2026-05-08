package com.example.mygame.database.entertainment_and_social

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gossip")
data class GossipEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val text: String,
    val turn: Int
)