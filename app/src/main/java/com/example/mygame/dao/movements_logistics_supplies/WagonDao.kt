package com.example.mygame.dao.movements_logistics_supplies

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.movements_logistics_supplies.WagonEntity

@Dao
interface WagonDao : BaseDao<WagonEntity> {
    @Query("SELECT * FROM wagon WHERE convoyId = :convoyId")
    suspend fun forConvoy(convoyId: Long): List<WagonEntity>
}