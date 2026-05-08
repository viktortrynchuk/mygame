package com.example.mygame.database.roles_and_offices

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "office")
data class OfficeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String?
)