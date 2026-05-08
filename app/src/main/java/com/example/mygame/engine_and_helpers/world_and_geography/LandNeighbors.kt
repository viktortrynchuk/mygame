package com.example.mygame.engine_and_helpers.world_and_geography

import com.example.mygame.database.world_and_geography.LandEntity

data class LandNeighbors(val land: LandEntity, val neighborIds: List<Long>)