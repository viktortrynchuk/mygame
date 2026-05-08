package com.example.mygame.database.nobility_titles_and_court

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_flag")
data class FavoriteFlag(
    @PrimaryKey val nobleId: Long,
    val isFavorite: Boolean
)