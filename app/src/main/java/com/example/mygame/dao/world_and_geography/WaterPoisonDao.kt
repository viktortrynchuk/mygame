package com.example.mygame.dao.world_and_geography

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.world_and_geography.WaterPoisonStateEntity

@Dao
interface WaterPoisonDao : BaseDao<WaterPoisonStateEntity> {
    @Query("SELECT * FROM water_poison_state WHERE landId = :landId")
    suspend fun get(landId: Long): WaterPoisonStateEntity?
}