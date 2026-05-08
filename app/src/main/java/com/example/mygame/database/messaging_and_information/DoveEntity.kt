package com.example.mygame.database.messaging_and_information

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "dove",
    indices = [Index(value = ["homeLandId"], name = "idx_dove_home")]
)
data class DoveEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val homeLandId: Long
)