package com.example.mygame.dao.nobility_titles_and_court

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.nobility_titles_and_court.TitleEntity

@Dao
interface TitleDao : BaseDao<TitleEntity> {
    @Query("SELECT * FROM title ORDER BY id")
    suspend fun list(): List<TitleEntity>
}