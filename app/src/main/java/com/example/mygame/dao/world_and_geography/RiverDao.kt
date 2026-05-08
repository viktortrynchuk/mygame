package com.example.mygame.dao.world_and_geography

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.world_and_geography.RiverEntity

@Dao
interface RiverDao : BaseDao<RiverEntity> {
    @Query("SELECT * FROM river WHERE id = :id")
    suspend fun get(id: Long): RiverEntity?
}