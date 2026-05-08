package com.example.mygame.engine_and_helpers.economy_resources_trade

import com.example.mygame.dao.economy_resources_trade.MarketDao
import com.example.mygame.dao.economy_resources_trade.TradeAgreementDao
import com.example.mygame.dao.economy_resources_trade.TradeRouteDao
import com.example.mygame.database.economy_resources_trade.MarketEntity
import com.example.mygame.database.economy_resources_trade.TradeAgreementEntity
import com.example.mygame.database.economy_resources_trade.TradeRouteEntity

interface MarketService {
    suspend fun marketOf(landId: Long): MarketEntity?
    suspend fun tradeRoutesFrom(originId: Long): List<TradeRouteEntity>
    suspend fun agreementBetween(countryA: Long, countryB: Long): TradeAgreementEntity?
    suspend fun setAgreement(countryA: Long, countryB: Long, terms: String): Long
}

class MarketServiceImpl(
    private val marketDao: MarketDao,
    private val routeDao: TradeRouteDao,
    private val agreementDao: TradeAgreementDao
) : MarketService {
    override suspend fun marketOf(landId: Long) = marketDao.inLand(landId)
    override suspend fun tradeRoutesFrom(originId: Long) = routeDao.fromOrigin(originId)
    override suspend fun agreementBetween(countryA: Long, countryB: Long) = agreementDao.between(countryA, countryB)
    override suspend fun setAgreement(countryA: Long, countryB: Long, terms: String): Long =
        agreementDao.upsert(
            TradeAgreementEntity(id = 0, countryA = minOf(countryA, countryB), countryB = maxOf(countryA, countryB), terms = terms)
        )
}