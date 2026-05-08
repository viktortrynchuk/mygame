package com.example.mygame.dao.population_and_society

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.population_and_society.MayorOrderEntity

@Dao
interface MayorOrderDao : BaseDao<MayorOrderEntity> {
    @Query("SELECT * FROM mayor_order WHERE landId = :landId")
    suspend fun byLand(landId: Long): List<MayorOrderEntity>
}