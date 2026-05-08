package com.example.mygame.dao.persistence_and_game_state

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.persistence_and_game_state.ScenarioGoalEntity

@Dao
interface ScenarioGoalDao : BaseDao<ScenarioGoalEntity> {
    @Query("SELECT * FROM scenario_goal WHERE scenarioId = :scenarioId")
    suspend fun goalsForScenario(scenarioId: Long): List<ScenarioGoalEntity>
}