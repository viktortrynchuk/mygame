package com.example.mygame.database.persistence_and_game_state

import androidx.room.Entity
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import androidx.room.PrimaryKey

@Parcelize
@Entity (tableName = "session")
data class CurrentSession(
    @PrimaryKey var scenarioId: Long,
    val actorId: Long,              // the player-controlled actor (ruler or noble)
    var currentLandId: Long,        // where the actor is physically located
    var startingView: Int,          // from where to start
    var currentTurn: Long           //current turn
) : Parcelable