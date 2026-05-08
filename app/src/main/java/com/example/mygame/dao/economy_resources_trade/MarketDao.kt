package com.example.mygame.dao.economy_resources_trade

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.economy_resources_trade.MarketEntity

@Dao
interface MarketDao : BaseDao<MarketEntity> {
    @Query("SELECT * FROM market WHERE landId = :landId")
    suspend fun inLand(landId: Long): MarketEntity?
}