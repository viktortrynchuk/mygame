package com.example.mygame.database.entertainment_and_social

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ball_event")
data class BallEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val turn: Int,
    val orderedByActorId: Long,
    val organizerActorId: Long,
    val venueStructureId: Long?,
    val plannedBallId: Long,
    val scale: String? = null,
    val useAlcohol: Boolean = false
)