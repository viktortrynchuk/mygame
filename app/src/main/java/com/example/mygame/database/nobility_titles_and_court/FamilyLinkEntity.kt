package com.example.mygame.database.nobility_titles_and_court

import androidx.room.Entity

@Entity(
    tableName = "family_link",
    primaryKeys = ["a", "b"]
)
data class FamilyLinkEntity(
    val a: Long,
    val b: Long,
    val relation: String
)