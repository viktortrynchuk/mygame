package com.example.mygame.database.entertainment_and_social

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "poison_attempt")
data class PoisonAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventType: String,
    val eventId: Long,
    val success: Boolean
)