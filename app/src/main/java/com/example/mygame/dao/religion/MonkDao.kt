package com.example.mygame.dao.religion

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.religion.MonkEntity

@Dao
interface MonkDao : BaseDao<MonkEntity> {
    @Query("SELECT * FROM monk WHERE traveling = 1")
    suspend fun traveling(): List<MonkEntity>
}