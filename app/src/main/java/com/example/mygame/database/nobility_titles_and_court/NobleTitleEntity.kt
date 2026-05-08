package com.example.mygame.database.nobility_titles_and_court

import androidx.room.Entity

@Entity(
    tableName = "noble_title",
    primaryKeys = ["nobleId", "titleId"]
)
data class NobleTitleEntity(
    val nobleId: Long,
    val titleId: Long
)