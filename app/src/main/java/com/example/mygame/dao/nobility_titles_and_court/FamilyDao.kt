package com.example.mygame.dao.nobility_titles_and_court

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.nobility_titles_and_court.FamilyLinkEntity

@Dao
interface FamilyDao : BaseDao<FamilyLinkEntity> {
    @Query("SELECT * FROM family_link WHERE a = :id OR b = :id")
    suspend fun links(id: Long): List<FamilyLinkEntity>
}