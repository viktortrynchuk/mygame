package com.example.mygame.database.nobility_titles_and_court

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "court_membership",
    indices = [Index(value = ["rulerId"], name = "idx_court_by_ruler")]
)
data class CourtMembershipEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nobleId: Long,
    val rulerId: Long
)