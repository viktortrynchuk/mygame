package com.example.mygame.engine_and_helpers

import androidx.room.PrimaryKey
import com.example.mygame.database.persistence_and_game_state.CurrentSession
import com.example.mygame.database.persistence_and_game_state.ScenarioEntity

object Constants {
    val emptySession = CurrentSession(scenarioId = 0, actorId = 0, currentLandId = 0, startingView = 0, currentTurn = 0)

    val Scenario1 = ScenarioEntity(
        scenarioId = 1,
        actorId = 1,
        currentLandId = 1,
        viewNum = 1,
        goal = "${Goals.KEEP_CROWN}:1",
        descr = "Scenario1",
        name = "Scenario1",
        isActive = false
    )

    val Scenario2 = ScenarioEntity(
        scenarioId = 2,
        actorId = 1,
        currentLandId = 1,
        viewNum = 2,
        goal = "${Goals.OWN_LAND}:1",
        descr = "Scenario2",
        name = "Scenario2",
        isActive = false
    )

    val Scenario3 = ScenarioEntity(
        scenarioId = 3,
        actorId = 10,
        currentLandId = 1,
        viewNum = 1,
        goal = "${Goals.OWN_LAND}:1",
        descr = "Scenario3",
        name = "Scenario3",
        isActive = false
    )
    val Scenario4 = ScenarioEntity(
        scenarioId = 4,
        actorId = 1,
        currentLandId = 1,
        viewNum = 1,
        goal = "${Goals.GET_RANK}:1",
        descr = "Scenario4",
        name = "Scenario4",
        isActive = false
    )
    val Scenario5 = ScenarioEntity(
        scenarioId = 5,
        actorId = 1,
        currentLandId = 1,
        viewNum = 1,
        goal = "${Goals.KILL_NOBLE}:5",
        descr = "Scenario5",
        name = "Scenario5",
        isActive = false
    )
    val Scenario6 = ScenarioEntity(
        scenarioId = 6,
        actorId = 1,
        currentLandId = 1,
        viewNum = 1,
        goal = "${Goals.KILL_NOBLE}:5",
        descr = "Scenario6",
        name = "Scenario6",
        isActive = false
    )
    val Scenario7 = ScenarioEntity(
        scenarioId = 7,
        actorId = 1,
        currentLandId = 1,
        viewNum = 1,
        goal = "${Goals.KILL_NOBLE}:5",
        descr = "Scenario7",
        name = "Scenario7",
        isActive = false
    )
    val Scenario8 = ScenarioEntity(
        scenarioId = 8,
        actorId = 1,
        currentLandId = 1,
        viewNum = 1,
        goal = "${Goals.KILL_NOBLE}:5",
        descr = "Scenario8",
        name = "Scenario8",
        isActive = false
    )
    val Scenario9 = ScenarioEntity(
        scenarioId = 9,
        actorId = 1,
        currentLandId = 1,
        viewNum = 1,
        goal = "${Goals.KILL_NOBLE}:5",
        descr = "Scenario9",
        name = "Scenario9",
        isActive = false
    )
    val Scenario10 = ScenarioEntity(
        scenarioId = 10,
        actorId = 1,
        currentLandId = 1,
        viewNum = 1,
        goal = "${Goals.KILL_NOBLE}:5",
        descr = "Scenario10",
        name = "Scenario10",
        isActive = false
    )
    val Scenario11 = ScenarioEntity(
        scenarioId = 11,
        actorId = 1,
        currentLandId = 1,
        viewNum = 1,
        goal = "${Goals.KILL_NOBLE}:5",
        descr = "Scenario11",
        name = "Scenario11",
        isActive = false
    )
    val Scenario12 = ScenarioEntity(
        scenarioId = 12,
        actorId = 1,
        currentLandId = 1,
        viewNum = 1,
        goal = "${Goals.KILL_NOBLE}:3",
        descr = "Scenario12",
        name = "Scenario12",
        isActive = false
    )

    val defaultMorale: Int = 50
}