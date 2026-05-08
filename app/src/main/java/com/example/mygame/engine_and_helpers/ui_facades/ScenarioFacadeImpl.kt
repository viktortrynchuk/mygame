package com.example.mygame.engine_and_helpers.ui_facades

import com.example.mygame.database.persistence_and_game_state.ScenarioEntity
import com.example.mygame.engine_and_helpers.persistence_and_game_state.ScenarioManager

interface ScenarioFacade {
    suspend fun available(): List<ScenarioEntity>
    suspend fun active(): ScenarioEntity?
    suspend fun activate(id: Long)
}

class ScenarioFacadeImpl(private val scenarios: ScenarioManager) : ScenarioFacade {
    override suspend fun available() = scenarios.list()
    override suspend fun active() = scenarios.active()
    override suspend fun activate(id: Long) = scenarios.activate(id)
}