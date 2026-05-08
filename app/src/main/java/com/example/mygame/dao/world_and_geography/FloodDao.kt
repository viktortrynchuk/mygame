package com.example.mygame.dao.world_and_geography

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.world_and_geography.FloodStateEntity

@Dao
interface FloodDao : BaseDao<FloodStateEntity> {
    @Query("SELECT * FROM flood_state WHERE landId = :landId")
    suspend fun get(landId: Long): FloodStateEntity?
}