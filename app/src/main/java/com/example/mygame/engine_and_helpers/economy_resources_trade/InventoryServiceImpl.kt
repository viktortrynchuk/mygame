package com.example.mygame.engine_and_helpers.economy_resources_trade

import com.example.mygame.dao.economy_resources_trade.ResourceProductionDao
import com.example.mygame.dao.economy_resources_trade.ResourceStockDao
import com.example.mygame.database.economy_resources_trade.ResourceProductionEntity
import com.example.mygame.database.economy_resources_trade.ResourceStockEntity

interface InventoryService {
    suspend fun stockIn(landId: Long): List<ResourceStockEntity>
    suspend fun setStock(landId: Long, itemId: String, qty: Int): Long
    suspend fun productionIn(landId: Long): List<ResourceProductionEntity>
}

class InventoryServiceImpl(
    private val stockDao: ResourceStockDao,
    private val prodDao: ResourceProductionDao
) : InventoryService {
    override suspend fun stockIn(landId: Long) = stockDao.forLand(landId)

    override suspend fun setStock(landId: Long, itemId: String, qty: Int): Long =
        stockDao.upsert(ResourceStockEntity(id = 0, landId = landId, itemId = itemId, qty = qty))

    override suspend fun productionIn(landId: Long) = prodDao.forLand(landId)
}