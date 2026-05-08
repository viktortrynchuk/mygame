package com.example.mygame.engine_and_helpers.ai

import com.example.mygame.dao.world_and_geography.LandDao
import com.example.mygame.engine_and_helpers.world_and_geography.LandNeighbors
import com.example.mygame.engine_and_helpers.world_and_geography.WorldRepository

/** Picks targets and simple paths for AI without heavy pathfinding. */
class TargetSelector(
    private val landDao: LandDao,
    private val landArmyIndex: LandArmyIndex
) {
    suspend fun pickBorderLandAgainst(countryId: Long, enemyCountryId: Long, world: WorldRepository): LandNeighbors? {
        val ours = world.landsByCountry(countryId)
        for (l in ours) {
            val neighbors = landDao.neighborIds(l.id)
            val enemyNeighbor = neighbors.firstOrNull { nid -> world.land(nid)?.countryId == enemyCountryId }
            if (enemyNeighbor != null) {
                return world.neighbors(l.id)
            }
        }
        return null
    }

    suspend fun pickRaidTarget(countryId: Long, enemyCountryId: Long, world: WorldRepository): LandNeighbors? =
        pickBorderLandAgainst(countryId, enemyCountryId, world)

    fun closestArmyIdToLand(landId: Long): Long = landArmyIndex.firstArmyNear(landId)
    fun firstArmyIdForCountry(countryId: Long): Long? = landArmyIndex.firstArmyForCountry(countryId)
}