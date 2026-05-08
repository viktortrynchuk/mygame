package com.example.mygame.engine_and_helpers.persistence_and_game_state

import com.example.mygame.dao.persistence_and_game_state.ScenarioDao
import com.example.mygame.dao.persistence_and_game_state.ScenarioGoalDao
import com.example.mygame.database.persistence_and_game_state.ScenarioEntity
import com.example.mygame.engine_and_helpers.TxRunner
import com.example.mygame.engine_and_helpers.foundations_core.AuditLogger

interface ScenarioManager {
    suspend fun list(): List<ScenarioEntity>
    suspend fun active(): ScenarioEntity?
    suspend fun activate(id: Long)
}

class ScenarioManagerImpl(
    private val dao: ScenarioDao,
    private val goalDao: ScenarioGoalDao,
    private val tx: TxRunner,
    private val audit: AuditLogger
) : ScenarioManager {
    override suspend fun list() = dao.list()
    override suspend fun active() = dao.active()

    override suspend fun activate(id: Long) = tx.tx {
        val prev = dao.active()?.scenarioId
        dao.activate(id)
        val goals = goalDao.goalsForScenario(id).map { it.type }
        audit.log("SCENARIO_ACTIVATED", mapOf("id" to id, "goals" to goals, "prev" to prev))
    }
}
