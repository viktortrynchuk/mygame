package com.example.mygame.engine_and_helpers.rebellion_banditry

import com.example.mygame.dao.rebellion_banditry.BanditGroupDao
import com.example.mygame.database.rebellion_banditry.BanditGroupEntity

/** Converts deserters or defeated units into bandits with proportional notoriety. */
class BanditryTransformer(private val banditDao: BanditGroupDao) {
    suspend fun fromDeserters(landId: Long, deserterCount: Int): Long {
        val notoriety = (deserterCount / 10).coerceAtLeast(1)
        return banditDao.upsert(BanditGroupEntity(id = 0, landId = landId, notoriety = notoriety))
    }
}