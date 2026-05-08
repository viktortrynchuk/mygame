package com.example.mygame.database.religion

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "religion_leader")
data class LeaderEntity(
    @PrimaryKey val religionId: Long,
    val priestId: Long
)