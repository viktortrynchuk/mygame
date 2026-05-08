package com.example.mygame.dao.world_and_geography

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.world_and_geography.TerrainEntity

@Dao
interface TerrainDao : BaseDao<TerrainEntity> {
    @Query("SELECT * FROM terrain WHERE code = :code")
    suspend fun get(code: String): TerrainEntity?
}