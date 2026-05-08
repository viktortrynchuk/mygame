package com.example.mygame.engine_and_helpers.world_and_geography

import com.example.mygame.dao.world_and_geography.BuildOrderDao
import com.example.mygame.database.world_and_geography.BuildOrderEntity
import com.example.mygame.engine_and_helpers.foundations_core.AuditLogger

interface StructurePlannerService {
    suspend fun queueForLand(landId: Long): List<BuildOrderEntity>
    suspend fun enqueue(landId: Long, structureType: String, builders: Int): Long
}

class StructurePlannerServiceImpl(
    private val buildOrderDao: BuildOrderDao,
    private val audit: AuditLogger
) : StructurePlannerService {
    override suspend fun queueForLand(landId: Long) = buildOrderDao.byLand(landId)

    override suspend fun enqueue(landId: Long, structureType: String, builders: Int): Long {
        val id = buildOrderDao.upsert(
            BuildOrderEntity(id = 0, landId = landId, structureType = structureType, assignedBuilders = builders)
        )
        audit.log("BUILD_ENQUEUE", mapOf("landId" to landId, "type" to structureType, "builders" to builders))
        return id
    }
}
