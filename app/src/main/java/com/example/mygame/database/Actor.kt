package com.example.mygame.database

import androidx.room.Entity
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.mygame.database.persistence_and_game_state.ScenarioEntity

@Parcelize
@Entity(
    tableName = "actor",
    primaryKeys = ["scenarioId", "actorId"],
    foreignKeys = [
        ForeignKey(
            entity = ScenarioEntity::class,
            parentColumns = ["scenarioId"],
            childColumns = ["scenarioId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["scenarioId"]),
        Index(value = ["scenarioId", "actorType"])
    ]
)
data class Actor(
    val scenarioId: Long,
    val actorId: Long,
    val name: String,
    val actorType: String = "GENERIC", // BARD, MERCHANT, SPY, ASSASSIN, RECRUITING_AGENT, GENERIC
    val notabilityLevel: Int = 1
) : Parcelable