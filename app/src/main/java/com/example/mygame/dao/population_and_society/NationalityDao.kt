package com.example.mygame.dao.population_and_society

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.population_and_society.NationalityEntity

@Dao
interface NationalityDao : BaseDao<NationalityEntity> {
    @Query("SELECT * FROM nationality ORDER BY id")
    suspend fun list(): List<NationalityEntity>
}