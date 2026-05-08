package com.example.mygame.database.nobility_titles_and_court

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "noble")
data class NobleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val nationalityId: Long,
    val religionId: Long
)