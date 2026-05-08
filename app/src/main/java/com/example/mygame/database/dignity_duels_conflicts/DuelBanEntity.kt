package com.example.mygame.database.dignity_duels_conflicts

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "duel_ban")
data class DuelBanEntity(
    @PrimaryKey val countryId: Long,
    val active: Boolean
)