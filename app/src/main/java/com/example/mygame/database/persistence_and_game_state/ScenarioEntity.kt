package com.example.mygame.database.persistence_and_game_state

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
@Entity (tableName = "scenario")
data class ScenarioEntity(
    @PrimaryKey (autoGenerate = true) var scenarioId: Long = 0,
    val actorId: Long,              // the player-controlled actor (ruler or noble)
    var currentLandId: Long,        // where the actor is physically located
    val goal: String,               // the scenario goal from the predefined list + the context of the goal
    val viewNum: Int,               // starting view
    val descr: String,               // description of scenario
    val name: String,
    val isActive : Boolean = false
) : Parcelable