package com.example.mygame.engine_and_helpers.foundations_core

import com.example.mygame.dao.foundations_core.IntegrityDao
import com.example.mygame.dao.foundations_core.TurnClockDao
import com.example.mygame.database.foundations_core.IntegrityViolationEntity
import com.example.mygame.engine_and_helpers.TimeProvider

interface IntegrityGuard { suspend fun report(code: String, details: String) }

class IntegrityGuardImpl(
    private val dao: IntegrityDao,
    private val clockDao: TurnClockDao,
    private val time: TimeProvider
) : IntegrityGuard {
    override suspend fun report(code: String, details: String) {
        val turn = clockDao.getSingleton()?.turn ?: 0
        dao.upsert(IntegrityViolationEntity(0, turn, code, details, time.nowMillis()))
    }
}
