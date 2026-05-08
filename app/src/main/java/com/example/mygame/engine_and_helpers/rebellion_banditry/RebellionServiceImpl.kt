package com.example.mygame.engine_and_helpers.rebellion_banditry

import com.example.mygame.dao.armies_units_warfare.ArmyDao
import com.example.mygame.dao.rebellion_banditry.BanditGroupDao
import com.example.mygame.dao.rebellion_banditry.OutlawDao
import com.example.mygame.dao.rebellion_banditry.RebelArmyDao
import com.example.mygame.dao.rebellion_banditry.RebellionDao
import com.example.mygame.dao.rebellion_banditry.SuppressionLogDao
import com.example.mygame.database.armies_units_warfare.ArmyEntity
import com.example.mygame.database.rebellion_banditry.BanditGroupEntity
import com.example.mygame.database.rebellion_banditry.OutlawEntity
import com.example.mygame.database.rebellion_banditry.RebelArmyEntity
import com.example.mygame.database.rebellion_banditry.RebellionEntity
import com.example.mygame.database.rebellion_banditry.SuppressionLogEntity

interface RebellionService {
    suspend fun startRebellion(landId: Long, cause: String, startedTurn: Int): Long
    suspend fun rebellionIn(landId: Long): RebellionEntity?
    suspend fun addRebelArmy(rebellionId: Long, landId: Long): Long
    suspend fun rebelArmies(rebellionId: Long): List<RebelArmyEntity>
    suspend fun banditsIn(landId: Long): List<BanditGroupEntity>
    suspend fun createBandits(landId: Long, notoriety: Int): Long
    suspend fun enlistOutlaw(groupId: Long, name: String): Long
    suspend fun suppressionLog(turn: Int, landId: Long, details: String): Long
}

/** Rebels are real armies linked via rebel_army.armyId so they reuse movement/battle systems. */
class RebellionServiceImpl(
    private val rebellionDao: RebellionDao,
    private val rebelArmyDao: RebelArmyDao,
    private val banditDao: BanditGroupDao,
    private val outlawDao: OutlawDao,
    private val suppressionDao: SuppressionLogDao,
    private val armyDao: ArmyDao
) : RebellionService {

    override suspend fun startRebellion(landId: Long, cause: String, startedTurn: Int): Long =
        rebellionDao.upsert(
            RebellionEntity(
                id = 0,
                landId = landId,
                cause = cause,
                startedTurn = startedTurn
            )
        )

    override suspend fun rebellionIn(landId: Long): RebellionEntity? =
        rebellionDao.inLand(landId)

    override suspend fun addRebelArmy(rebellionId: Long, landId: Long): Long {
        val armyId = armyDao.upsert(
            ArmyEntity(
                id = 0,
                name = "Rebel Militia",
                countryId = null,          // or a special faction id if your schema prefers non-null
                landId = landId,
                commanderActorId = null,
                morale = 50
            )
        )
        return rebelArmyDao.upsert(
            RebelArmyEntity(
                id = 0,
                rebellionId = rebellionId,
                armyId = armyId,
                role = "MAIN",
                createdTurn = 0,
                disbandedTurn = null
            )
        )
    }

    override suspend fun rebelArmies(rebellionId: Long): List<RebelArmyEntity> =
        rebelArmyDao.forRebellion(rebellionId)

    override suspend fun banditsIn(landId: Long): List<BanditGroupEntity> =
        banditDao.inLand(landId)

    override suspend fun createBandits(landId: Long, notoriety: Int): Long =
        banditDao.upsert(BanditGroupEntity(id = 0, landId = landId, notoriety = notoriety))

    override suspend fun enlistOutlaw(groupId: Long, name: String): Long =
        outlawDao.upsert(OutlawEntity(id = 0, banditGroupId = groupId, name = name))

    override suspend fun suppressionLog(turn: Int, landId: Long, details: String): Long =
        suppressionDao.upsert(SuppressionLogEntity(id = 0, turn = turn, landId = landId, details = details))
}