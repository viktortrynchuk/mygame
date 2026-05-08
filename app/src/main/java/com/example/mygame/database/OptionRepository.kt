package com.example.mygame.database

import com.example.mygame.dao.persistence_and_game_state.ScenarioDao
import javax.inject.Inject

class OptionRepository @Inject constructor(
    private val dao: ScenarioDao
) {
    suspend fun loadOptions() = dao.getAllScenarios()
}