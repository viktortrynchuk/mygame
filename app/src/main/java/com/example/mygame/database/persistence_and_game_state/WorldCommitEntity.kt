package com.example.mygame.database.persistence_and_game_state

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "world_commit",
    indices = [Index(value = ["turn"], name = "idx_world_commit_turn")]
)
data class WorldCommitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val turn: Int,
    val checksum: String,
    val createdAt: Long
)
