package com.example.mygame.engine_and_helpers.armies_units_warfare

import com.example.mygame.dao.armies_units_warfare.BattleDao
import com.example.mygame.dao.armies_units_warfare.BattleParticipantDao
import com.example.mygame.dao.armies_units_warfare.SiegeDao
import com.example.mygame.database.armies_units_warfare.BattleEntity
import com.example.mygame.database.armies_units_warfare.BattleParticipantEntity
import com.example.mygame.database.armies_units_warfare.SiegeEntity

interface WarfareService {
    suspend fun startBattle(landId: Long, startedTurn: Int): Long
    suspend fun registerParticipant(battleId: Long, armyId: Long, side: String): Long
    suspend fun battlesInTurn(turn: Int): List<BattleEntity>
    suspend fun startSiege(landId: Long, attackerArmyId: Long, defenderArmyId: Long?, startedTurn: Int): Long
}

class WarfareServiceImpl(
    private val battleDao: BattleDao,
    private val participantDao: BattleParticipantDao,
    private val siegeDao: SiegeDao
) : WarfareService {
    override suspend fun startBattle(landId: Long, startedTurn: Int): Long =
        battleDao.upsert(BattleEntity(id = 0, landId = landId, turn = null, startedTurn = startedTurn))

    override suspend fun registerParticipant(battleId: Long, armyId: Long, side: String): Long =
        participantDao.upsert(BattleParticipantEntity(id = 0, battleId = battleId, armyId = armyId, side = side))

    override suspend fun battlesInTurn(turn: Int) = battleDao.inTurn(turn)

    override suspend fun startSiege(landId: Long, attackerArmyId: Long, defenderArmyId: Long?, startedTurn: Int): Long =
        siegeDao.upsert(
            SiegeEntity(id = 0, landId = landId, attackerArmyId = attackerArmyId, defenderArmyId = defenderArmyId, startedTurn = startedTurn)
        )
}