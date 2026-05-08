package com.example.mygame.engine_and_helpers.ai

/**
 * Lightweight index that the game loop can refresh each turn to allow AI lookups without new DAOs.
 */
class LandArmyIndex(
    private val map: MutableMap<Long, MutableList<Long>> = mutableMapOf(), // landId -> armyIds
    private val byCountry: MutableMap<Long, MutableList<Long>> = mutableMapOf() // countryId -> armyIds
) {
    fun clear() { map.clear(); byCountry.clear() }
    fun indexArmy(armyId: Long, landId: Long, countryId: Long) {
        map.getOrPut(landId) { mutableListOf() }.add(armyId)
        byCountry.getOrPut(countryId) { mutableListOf() }.add(armyId)
    }
    fun firstArmyNear(landId: Long): Long = map[landId]?.firstOrNull() ?: map.values.firstOrNull()?.firstOrNull() ?: -1
    fun firstArmyForCountry(countryId: Long): Long? = byCountry[countryId]?.firstOrNull()
}