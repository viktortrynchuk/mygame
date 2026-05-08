package com.example.mygame.database.religion

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "religious_clash_log",
    indices = [Index(value = ["landId"], name = "idx_religious_clash_land")]
)
data class ReligiousClashLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val turn: Int,
    val details: String
)