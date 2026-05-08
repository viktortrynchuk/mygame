package com.example.mygame.dao.politics_diplomacy_succession

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.politics_diplomacy_succession.CountryEntity

@Dao
interface CountryDao : BaseDao<CountryEntity> {
    @Query("SELECT * FROM country ORDER BY id")
    suspend fun list(): List<CountryEntity>

    @Query("SELECT COUNT(*) FROM country")
    suspend fun countAll(): Int
}