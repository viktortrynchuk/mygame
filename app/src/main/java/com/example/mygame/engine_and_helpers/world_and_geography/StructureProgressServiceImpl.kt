package com.example.mygame.engine_and_helpers.world_and_geography

import com.example.mygame.dao.world_and_geography.StructureDao
import com.example.mygame.dao.world_and_geography.StructureEffectDao
import com.example.mygame.dao.world_and_geography.StructureProgressDao
import com.example.mygame.database.world_and_geography.StructureEffectEntity
import com.example.mygame.database.world_and_geography.StructureEntity
import com.example.mygame.database.world_and_geography.StructureProgressEntity

interface StructureProgressService {
    suspend fun structures(landId: Long): List<StructureEntity>
    suspend fun progress(structureId: Long): StructureProgressEntity?
    suspend fun updateProgress(structureId: Long, total: Int, done: Int)
    suspend fun effectsFor(type: String): List<StructureEffectEntity>
}

class StructureProgressServiceImpl(
    private val structureDao: StructureDao,
    private val progressDao: StructureProgressDao,
    private val effectDao: StructureEffectDao
) : StructureProgressService {
    override suspend fun structures(landId: Long) = structureDao.byLand(landId)
    override suspend fun progress(structureId: Long) = progressDao.get(structureId)
    override suspend fun updateProgress(structureId: Long, total: Int, done: Int) {
        progressDao.upsert(StructureProgressEntity(structureId = structureId, totalEffort = total, doneEffort = done))
    }
    override suspend fun effectsFor(type: String) = effectDao.byType(type)
}
