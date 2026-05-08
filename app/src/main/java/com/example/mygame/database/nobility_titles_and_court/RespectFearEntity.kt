package com.example.mygame.database.nobility_titles_and_court

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "respect_fear")
data class RespectFearEntity(
    @PrimaryKey val nobleId: Long,
    val respect: Int,
    val fear: Int
)