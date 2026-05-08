package com.example.mygame.database.messaging_and_information

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "messenger",
    indices = [Index(value = ["landId"], name = "idx_messenger_land")]
)
data class MessengerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val loyal: Boolean
)