package com.example.mygame.database.religion

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "temple",
    indices = [Index(value = ["landId"], name = "idx_temple_land")]
)
data class TempleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val religionId: Long
)