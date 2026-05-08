package com.example.mygame.engine_and_helpers.population_and_society

import com.example.mygame.dao.population_and_society.HouseholdDao
import com.example.mygame.dao.population_and_society.MayorDao
import com.example.mygame.dao.population_and_society.MayorOrderDao
import com.example.mygame.dao.population_and_society.PopulationDao
import com.example.mygame.dao.population_and_society.SatisfactionDao
import com.example.mygame.dao.population_and_society.WorkDao
import com.example.mygame.database.population_and_society.HouseholdEntity
import com.example.mygame.database.population_and_society.MayorEntity
import com.example.mygame.database.population_and_society.MayorOrderEntity
import com.example.mygame.database.population_and_society.PopulationStatEntity
import com.example.mygame.database.population_and_society.SatisfactionEntity
import com.example.mygame.database.population_and_society.WorkAssignmentEntity

interface PopulationService {
    suspend fun householdsIn(landId: Long): List<HouseholdEntity>
    suspend fun populationOf(landId: Long): PopulationStatEntity?
    suspend fun satisfactionOf(landId: Long): SatisfactionEntity?
    suspend fun setSatisfaction(landId: Long, value: Int)
    suspend fun mayorOf(landId: Long): MayorEntity?
    suspend fun ordersForLand(landId: Long): List<MayorOrderEntity>
    suspend fun workIn(landId: Long): List<WorkAssignmentEntity>
    suspend fun assignWork(assignment: WorkAssignmentEntity): Long
}

class PopulationServiceImpl(
    private val householdDao: HouseholdDao,
    private val populationDao: PopulationDao,
    private val satisfactionDao: SatisfactionDao,
    private val mayorDao: MayorDao,
    private val mayorOrderDao: MayorOrderDao,
    private val workDao: WorkDao
) : PopulationService {
    override suspend fun householdsIn(landId: Long) = householdDao.byLand(landId)
    override suspend fun populationOf(landId: Long) = populationDao.get(landId)
    override suspend fun satisfactionOf(landId: Long) = satisfactionDao.get(landId)
    override suspend fun setSatisfaction(landId: Long, value: Int) {
        satisfactionDao.upsert(SatisfactionEntity(landId = landId, level = value))
    }
    override suspend fun mayorOf(landId: Long) = mayorDao.get(landId)
    override suspend fun ordersForLand(landId: Long) = mayorOrderDao.byLand(landId)
    override suspend fun workIn(landId: Long) = workDao.byLand(landId)
    override suspend fun assignWork(assignment: WorkAssignmentEntity): Long = workDao.upsert(assignment)
}