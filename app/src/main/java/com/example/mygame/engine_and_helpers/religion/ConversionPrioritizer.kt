package com.example.mygame.engine_and_helpers.religion

import com.example.mygame.dao.religion.ConversionDao
import com.example.mygame.dao.religion.ReligiousClashDao

/** Scores a conversion task priority using simple heuristics. */
class ConversionPrioritizer(
    private val conversionDao: ConversionDao,
    private val religiousClashDao: ReligiousClashDao
) {
    suspend fun score(targetType: String, targetRef: Long): Int {
        val queued = conversionDao.byTarget(targetType, targetRef).size
        val clashes = when (targetType) {
            "LAND" -> religiousClashDao.byLand(targetRef).size
            else -> 0
        }
        return (clashes * 15 + (if (queued == 0) 20 else -10)).coerceIn(0, 100)
    }
}