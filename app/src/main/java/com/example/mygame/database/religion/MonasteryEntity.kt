package com.example.mygame.database.religion

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "monastery",
    indices = [Index(value = ["landId"], name = "idx_monastery_land")]
)
data class MonasteryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val religionId: Long
)