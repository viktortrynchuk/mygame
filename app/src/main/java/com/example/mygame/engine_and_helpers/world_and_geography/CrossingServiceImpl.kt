package com.example.mygame.engine_and_helpers.world_and_geography

import com.example.mygame.dao.world_and_geography.CrossingDao
import com.example.mygame.database.world_and_geography.CrossingEntity

interface CrossingService {
    suspend fun crossingsIn(landId: Long): List<CrossingEntity>
}

class CrossingServiceImpl(private val dao: CrossingDao) : CrossingService {
    override suspend fun crossingsIn(landId: Long) = dao.byLand(landId)
}
