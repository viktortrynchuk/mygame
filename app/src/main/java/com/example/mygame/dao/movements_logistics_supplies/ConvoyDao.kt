package com.example.mygame.dao.movements_logistics_supplies

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.movements_logistics_supplies.ConvoyEntity

@Dao
interface ConvoyDao : BaseDao<ConvoyEntity> {
    @Query("SELECT * FROM convoy WHERE landId = :landId")
    suspend fun inLand(landId: Long): List<ConvoyEntity>
}