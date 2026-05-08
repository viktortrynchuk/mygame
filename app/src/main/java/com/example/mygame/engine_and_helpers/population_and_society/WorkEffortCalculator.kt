package com.example.mygame.engine_and_helpers.population_and_society

import com.example.mygame.dao.population_and_society.WorkDao

/** Simple helper to derive total productive effort in a land for the current turn. */
class WorkEffortCalculator(private val workDao: WorkDao) {
    suspend fun totalEffort(landId: Long): Int = workDao.byLand(landId).sumOf { it.effortPerTurn }
}
