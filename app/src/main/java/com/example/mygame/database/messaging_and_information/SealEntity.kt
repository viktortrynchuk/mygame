package com.example.mygame.database.messaging_and_information

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "seal",
    indices = [Index(value = ["messageId"], name = "idx_seal_msg")]
)
data class SealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val messageId: Long,
    val accuracy: Int
)