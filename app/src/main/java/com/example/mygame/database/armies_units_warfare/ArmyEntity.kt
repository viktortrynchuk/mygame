package com.example.mygame.database.armies_units_warfare

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Variant E: full army row that works across movement, logistics, and battle.
 * countryId is nullable to allow "factionless" rebels; use a special faction id if you prefer non-null.
 */
@Entity(
    tableName = "army",
    indices = [
        Index("countryId"),
        Index("landId"),
        Index("commanderActorId")
    ]
)
data class ArmyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val countryId: Long?,        // null for rebel/bandit armies, or use dedicated faction id
    val landId: Long,            // current location
    val commanderActorId: Long?, // nullable if no commander
    val morale: Int              // 0..100
)
