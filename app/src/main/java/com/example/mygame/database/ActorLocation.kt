package com.example.mygame.database
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "actor_location",
    primaryKeys = ["scenarioId", "actorId"],                // one row per actor
    foreignKeys = [
        ForeignKey(
            entity = Actor::class,
            parentColumns = ["scenarioId", "actorId"],      // 👈 composite parent key
            childColumns  = ["scenarioId", "actorId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["scenarioId", "actorId"])            // composite index for FK
    ]
)
data class ActorLocation(
    val scenarioId: Long,
    val actorId: Long,
    val locationId: Long                                    // the actual location
)