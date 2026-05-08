package com.example.mygame.dao.world_and_geography

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.world_and_geography.CrossingEntity

@Dao
interface CrossingDao : BaseDao<CrossingEntity> {
    @Query("SELECT * FROM crossing WHERE landId = :landId")
    suspend fun byLand(landId: Long): List<CrossingEntity>
}