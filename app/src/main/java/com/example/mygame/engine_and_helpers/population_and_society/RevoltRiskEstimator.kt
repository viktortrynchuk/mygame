package com.example.mygame.engine_and_helpers.population_and_society

import com.example.mygame.dao.population_and_society.MayorOrderDao
import com.example.mygame.dao.population_and_society.SatisfactionDao

/** Computes a naive revolt risk based on satisfaction + orders queued. */
class RevoltRiskEstimator(
    private val satisfactionDao: SatisfactionDao,
    private val mayorOrderDao: MayorOrderDao
) {
    suspend fun riskPercent(landId: Long): Int {
        val s = satisfactionDao.get(landId)?.level ?: 50
        val orders = mayorOrderDao.byLand(landId).size
        val base = (100 - s) / 2
        return (base + orders).coerceIn(0, 100)
    }
}