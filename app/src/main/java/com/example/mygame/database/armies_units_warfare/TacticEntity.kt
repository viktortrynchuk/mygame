package com.example.mygame.database.armies_units_warfare

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tactic")
data class TacticEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val terrain: String
)