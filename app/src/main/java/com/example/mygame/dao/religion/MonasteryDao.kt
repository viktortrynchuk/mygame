package com.example.mygame.dao.religion

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.religion.MonasteryEntity

@Dao
interface MonasteryDao : BaseDao<MonasteryEntity> {
    @Query("SELECT * FROM monastery WHERE landId = :landId")
    suspend fun byLand(landId: Long): List<MonasteryEntity>
}