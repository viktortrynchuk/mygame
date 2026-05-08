package com.example.mygame.engine_and_helpers.ui_facades

import com.example.mygame.database.economy_resources_trade.MarketEntity
import com.example.mygame.database.population_and_society.PopulationStatEntity
import com.example.mygame.database.world_and_geography.FortificationEntity
import com.example.mygame.database.world_and_geography.LandEntity
import com.example.mygame.database.world_and_geography.StructureEntity
import com.example.mygame.engine_and_helpers.world_and_geography.Ownership

data class LandSummary(
    val land: LandEntity,
    val owner: Ownership?,
    val neighbors: List<Long>,
    val satisfaction: Int?,
    val population: PopulationStatEntity?,
    val structures: List<StructureEntity>,
    val fortification: FortificationEntity?,
    val floodTurns: Int?,
    val poisonedUntil: Int?,
    val market: MarketEntity?,
    val temples: Int,
    val monasteries: Int
)