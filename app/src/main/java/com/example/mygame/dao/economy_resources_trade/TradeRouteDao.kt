package com.example.mygame.dao.economy_resources_trade

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.economy_resources_trade.TradeRouteEntity

@Dao
interface TradeRouteDao : BaseDao<TradeRouteEntity> {
    @Query("SELECT * FROM trade_route WHERE originId = :originId")
    suspend fun fromOrigin(originId: Long): List<TradeRouteEntity>
}