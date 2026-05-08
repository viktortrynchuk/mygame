package com.example.mygame.dao.nobility_titles_and_court

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.nobility_titles_and_court.NobleEntity

@Dao
interface NobleDao : BaseDao<NobleEntity> {
    @Query("SELECT * FROM noble WHERE id = :id")
    suspend fun get(id: Long): NobleEntity?

    @Query("SELECT * FROM noble")
    suspend fun getAll(): List<NobleEntity>
}