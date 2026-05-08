package com.example.mygame.engine_and_helpers.world_and_geography

import com.example.mygame.dao.world_and_geography.DamDao
import com.example.mygame.dao.world_and_geography.FloodDao
import com.example.mygame.dao.world_and_geography.WaterPoisonDao
import com.example.mygame.database.world_and_geography.DamEntity
import com.example.mygame.database.world_and_geography.FloodStateEntity
import com.example.mygame.database.world_and_geography.WaterPoisonStateEntity

interface WaterControlService {
    suspend fun damsIn(landId: Long): List<DamEntity>
    suspend fun floodState(landId: Long): FloodStateEntity?
    suspend fun setFloodTurns(landId: Long, turns: Int)
    suspend fun poisonState(landId: Long): WaterPoisonStateEntity?
}

class WaterControlServiceImpl(
    private val damDao: DamDao,
    private val floodDao: FloodDao,
    private val poisonDao: WaterPoisonDao
) : WaterControlService {
    override suspend fun damsIn(landId: Long) = damDao.byLand(landId)
    override suspend fun floodState(landId: Long) = floodDao.get(landId)
    override suspend fun setFloodTurns(landId: Long, turns: Int) {
        floodDao.upsert(FloodStateEntity(landId = landId, floodTurns = turns))
    }
    override suspend fun poisonState(landId: Long) = poisonDao.get(landId)
}
