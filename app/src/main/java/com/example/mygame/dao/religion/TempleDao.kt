package com.example.mygame.dao.religion

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.religion.TempleEntity

@Dao
interface TempleDao : BaseDao<TempleEntity> {
    @Query("SELECT * FROM temple WHERE landId = :landId")
    suspend fun byLand(landId: Long): List<TempleEntity>
}