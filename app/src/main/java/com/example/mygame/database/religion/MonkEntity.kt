package com.example.mygame.database.religion

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monk")
data class MonkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val monasteryId: Long?,
    val traveling: Boolean
)