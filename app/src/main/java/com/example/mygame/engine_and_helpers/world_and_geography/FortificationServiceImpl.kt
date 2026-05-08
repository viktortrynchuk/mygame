package com.example.mygame.engine_and_helpers.world_and_geography

import com.example.mygame.dao.world_and_geography.FortificationDao
import com.example.mygame.dao.world_and_geography.SiegeworkDao
import com.example.mygame.database.world_and_geography.FortificationEntity
import com.example.mygame.database.world_and_geography.SiegeworkEntity

interface FortificationService {
    suspend fun fortification(landId: Long): FortificationEntity?
    suspend fun siegeworks(landId: Long): List<SiegeworkEntity>
}

class FortificationServiceImpl(
    private val fortDao: FortificationDao,
    private val siegeDao: SiegeworkDao
) : FortificationService {
    override suspend fun fortification(landId: Long) = fortDao.byLand(landId)
    override suspend fun siegeworks(landId: Long) = siegeDao.byLand(landId)
}
