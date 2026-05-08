package com.example.mygame.dao.economy_resources_trade

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.economy_resources_trade.TradeAgreementEntity

@Dao
interface TradeAgreementDao : BaseDao<TradeAgreementEntity> {
    @Query("SELECT * FROM trade_agreement WHERE countryA = :countryA AND countryB = :countryB")
    suspend fun between(countryA: Long, countryB: Long): TradeAgreementEntity?
}