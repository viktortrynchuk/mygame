package com.example.mygame.database.religion

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "priest",
    indices = [Index(value = ["religionId"], name = "idx_priest_rel")]
)
data class PriestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val religionId: Long,
    val rankId: Long,
    val nobleRef: Long?
)