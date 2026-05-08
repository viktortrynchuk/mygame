package com.example.mygame.engine_and_helpers.ui_facades


import com.example.mygame.dao.economy_resources_trade.MarketDao
import com.example.mygame.dao.population_and_society.PopulationDao
import com.example.mygame.dao.population_and_society.SatisfactionDao
import com.example.mygame.dao.religion.MonasteryDao
import com.example.mygame.dao.religion.TempleDao
import com.example.mygame.dao.world_and_geography.FloodDao
import com.example.mygame.dao.world_and_geography.FortificationDao
import com.example.mygame.dao.world_and_geography.LandDao
import com.example.mygame.dao.world_and_geography.StructureDao
import com.example.mygame.dao.world_and_geography.WaterPoisonDao
import com.example.mygame.engine_and_helpers.world_and_geography.WorldRepository
import javax.inject.Inject
import javax.inject.Singleton

interface LandFacade {
    suspend fun summary(landId: Long): LandSummary?
    suspend fun countryMap(countryId: Long): List<LandSummary>
}

@Singleton
class LandFacadeImpl @Inject constructor(
    private val world: WorldRepository,
    private val landDao: LandDao,
    private val structDao: StructureDao,
    private val fortDao: FortificationDao,
    private val floodDao: FloodDao,
    private val poisonDao: WaterPoisonDao,
    private val popDao: PopulationDao,
    private val satDao: SatisfactionDao,
    private val marketDao: MarketDao,
    private val templeDao: TempleDao,
    private val monasteryDao: MonasteryDao
) : LandFacade {
    override suspend fun summary(landId: Long): LandSummary? {
        val land = world.land(landId) ?: return null
        val owner = world.ownershipOf(landId)
        val neighbors = landDao.neighborIds(landId)
        val satisfaction = satDao.get(landId)?.level
        val population = popDao.get(landId)
        val structures = structDao.byLand(landId)
        val fort = fortDao.byLand(landId)
        val flood = floodDao.get(landId)?.floodTurns
        val poisoned = poisonDao.get(landId)?.poisonedUntilTurn
        val market = marketDao.inLand(landId)
        val temples = templeDao.byLand(landId).size
        val monasteries = monasteryDao.byLand(landId).size
        return LandSummary(
            land, owner, neighbors, satisfaction, population, structures, fort, flood, poisoned, market, temples, monasteries
        )
    }

    override suspend fun countryMap(countryId: Long): List<LandSummary> =
        world.landsByCountry(countryId).mapNotNull { summary(it.id) }
}