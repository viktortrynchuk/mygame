package com.example.mygame.engine_and_helpers.world_and_geography

import com.example.mygame.dao.world_and_geography.RiverDao
import com.example.mygame.dao.world_and_geography.RiverSegmentDao
import com.example.mygame.database.world_and_geography.RiverEntity
import com.example.mygame.database.world_and_geography.RiverSegmentEntity

interface RiverSystemService {
    suspend fun riverPath(riverId: Long): List<RiverSegmentEntity>
    suspend fun riversAt(landId: Long): List<RiverEntity>
}

class RiverSystemServiceImpl(
    private val riverDao: RiverDao,
    private val segmentDao: RiverSegmentDao
) : RiverSystemService {
    override suspend fun riverPath(riverId: Long) = segmentDao.segments(riverId)

    override suspend fun riversAt(landId: Long): List<RiverEntity> {
        // Cheap lookup by scanning segments -> riverIds -> rivers (optimize with views if needed)
        val rivers = mutableListOf<RiverEntity>()
        // In a real impl, add DAO for segmentsByLand. Here we derive by querying all rivers (caller can cache)
        // Placeholder: return empty when no direct DAO available
        return rivers
    }
}
