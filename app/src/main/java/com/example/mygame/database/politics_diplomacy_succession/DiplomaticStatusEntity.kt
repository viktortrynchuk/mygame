package com.example.mygame.database.politics_diplomacy_succession

import androidx.room.Entity

@Entity(
    tableName = "diplomatic_status",
    primaryKeys = ["a", "b"]
)
data class DiplomaticStatusEntity(
    val a: Long,
    val b: Long,
    val status: String
)