package com.example.mygame.database.world_and_geography

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "siegework",
    indices = [Index(value = ["landId"], name = "idx_siegework_land")]
)
data class SiegeworkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val type: String,
    val progress: Int
)