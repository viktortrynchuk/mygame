package com.example.mygame.database.roles_and_offices

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chancellor")
data class ChancellorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nobleId: Long,
    val rulerId: Long
)