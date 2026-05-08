package com.example.mygame.database.justice_and_court

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "punishment")
data class PunishmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val verdictId: Long,
    val type: String
)