package com.example.mygame.dao.world_and_geography

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.world_and_geography.SiegeworkEntity

@Dao
interface SiegeworkDao : BaseDao<SiegeworkEntity> {
    @Query("SELECT * FROM siegework WHERE landId = :landId")
    suspend fun byLand(landId: Long): List<SiegeworkEntity>
}