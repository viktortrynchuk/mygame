package com.example.mygame.database.messaging_and_information

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "interception_log",
    indices = [Index(value = ["messageId"], name = "idx_interception_msg")]
)
data class InterceptionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val messageId: Long,
    val turn: Int,
    val details: String
)