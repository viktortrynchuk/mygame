package com.example.mygame.database.nobility_titles_and_court

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "court_position",
    indices = [Index(value = ["type"], name = "idx_court_position_type")]
)
data class CourtPositionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nobleId: Long,
    val type: String
)