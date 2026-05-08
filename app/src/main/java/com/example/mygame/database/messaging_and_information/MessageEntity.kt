package com.example.mygame.database.messaging_and_information

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "message",
    indices = [
        Index(value = ["toActorId"], name = "idx_message_to"),
        Index(value = ["fromActorId"], name = "idx_message_from")
    ]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fromActorId: Long,
    val toActorId: Long?,
    val toRole: String?,
    val type: MessageType,
    val stampBroken: Boolean,
    val sentTurn: Int,
    val payload: String
)