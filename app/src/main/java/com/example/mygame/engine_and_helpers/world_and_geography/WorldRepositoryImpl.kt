package com.example.mygame.engine_and_helpers.world_and_geography

import com.example.mygame.dao.world_and_geography.LandDao
import com.example.mygame.dao.world_and_geography.OwnershipDao
import com.example.mygame.dao.world_and_geography.TerrainDao
import com.example.mygame.database.world_and_geography.LandEntity
import com.example.mygame.database.world_and_geography.TerrainEntity
import javax.inject.Inject
import javax.inject.Singleton

interface WorldRepository {
    suspend fun land(id: Long): LandEntity?
    suspend fun landsByCountry(countryId: Long): List<LandEntity>
    suspend fun neighbors(id: Long): LandNeighbors?
    suspend fun ownershipOf(landId: Long): Ownership?
    suspend fun setOwner(landId: Long, ownerType: String, ownerRef: Long)
    suspend fun terrain(code: String): TerrainEntity?
}

@Singleton
class WorldRepositoryImpl @Inject constructor(
    private val landDao: LandDao,
    private val ownershipDao: OwnershipDao,
    private val terrainDao: TerrainDao
) : WorldRepository {
    override suspend fun land(id: Long) = landDao.get(id)
    override suspend fun landsByCountry(countryId: Long) = landDao.byCountry(countryId)
    override suspend fun neighbors(id: Long): LandNeighbors? = landDao.get(id)?.let {
        LandNeighbors(it, landDao.neighborIds(id))
    }
    override suspend fun ownershipOf(landId: Long): Ownership? = ownershipDao.ownerOf(landId)?.let {
        Ownership(it.landId, it.ownerType, it.ownerRef)
    }
    override suspend fun setOwner(landId: Long, ownerType: String, ownerRef: Long) {
        ownershipDao.setOwner(landId, ownerType, ownerRef)
    }
    override suspend fun terrain(code: String) = terrainDao.get(code)
}
