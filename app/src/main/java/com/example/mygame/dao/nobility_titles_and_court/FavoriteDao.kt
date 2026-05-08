package com.example.mygame.dao.nobility_titles_and_court

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.nobility_titles_and_court.FavoriteFlag

@Dao
interface FavoriteDao : BaseDao<FavoriteFlag> {
    @Query("SELECT * FROM favorite_flag WHERE nobleId = :id")
    suspend fun get(id: Long): FavoriteFlag?
}