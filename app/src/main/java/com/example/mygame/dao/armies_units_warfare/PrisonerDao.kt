package com.example.mygame.dao.armies_units_warfare

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.armies_units_warfare.PrisonerEntity

@Dao
interface PrisonerDao : BaseDao<PrisonerEntity>{
    @Query("SELECT * FROM prisoner")
    suspend fun getAll(): List<PrisonerEntity>
}