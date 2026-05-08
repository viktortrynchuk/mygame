package com.example.mygame.database.messaging_and_information

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post_office")
data class PostOfficeEntity(
    @PrimaryKey val landId: Long,
    val messengerCapacity: Int
)