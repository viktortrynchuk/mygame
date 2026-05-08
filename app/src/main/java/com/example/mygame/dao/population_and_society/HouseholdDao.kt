package com.example.mygame.dao.population_and_society

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.population_and_society.HouseholdEntity

@Dao
interface HouseholdDao : BaseDao<HouseholdEntity> {
    @Query("SELECT * FROM household WHERE landId = :landId")
    suspend fun byLand(landId: Long): List<HouseholdEntity>
}