package com.example.mygame.dao.economy_resources_trade

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.economy_resources_trade.ResourceStockEntity

@Dao
interface ResourceStockDao : BaseDao<ResourceStockEntity> {
    @Query("SELECT * FROM resource_stock WHERE landId = :landId")
    suspend fun forLand(landId: Long): List<ResourceStockEntity>
}
