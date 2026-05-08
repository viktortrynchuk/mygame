package com.example.mygame.database.nobility_titles_and_court

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "title")
data class TitleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val sequenced: Boolean
)