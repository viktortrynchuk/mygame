package com.example.mygame.database.persistence_and_game_state

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "scenario_goal",
    indices = [Index(value = ["scenarioId"], name = "idx_scenario_goal_scn")]
)
data class ScenarioGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val scenarioId: Long,
    val type: String,
    val targetRef: String?
)