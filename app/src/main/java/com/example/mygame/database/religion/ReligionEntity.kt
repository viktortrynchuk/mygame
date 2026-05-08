package com.example.mygame.database.religion

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "religion")
data class ReligionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)