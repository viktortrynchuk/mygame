package com.example.mygame.engine_and_helpers.foundations_core

import com.example.mygame.dao.foundations_core.TurnClockDao
import com.example.mygame.database.foundations_core.TurnClockEntity
import com.example.mygame.engine_and_helpers.TxRunner
import javax.inject.Inject
import javax.inject.Singleton

interface TurnClockService {
    suspend fun read(): TurnClockEntity
    suspend fun tick(): TurnClockEntity
}

@Singleton
class TurnClockServiceImpl @Inject constructor(
    private val dao: TurnClockDao,
    private val tx: TxRunner,
    private val audit: AuditLogger
) : TurnClockService {

    override suspend fun read(): TurnClockEntity =
        dao.getSingleton() ?: TurnClockEntity(turn = 0, isNight = false, season = "SPRING", seed = 0)

    override suspend fun tick(): TurnClockEntity = tx.tx {
        val next = dao.incrementTurn()
        audit.log("TURN_TICK", mapOf("turn" to next.turn, "isNight" to next.isNight))
        next
    }
}
