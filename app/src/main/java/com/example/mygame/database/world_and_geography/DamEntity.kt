package com.example.mygame.database.world_and_geography

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "dam",
    indices = [Index(value = ["landId"], name = "idx_dam_land")]
)
data class DamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val riverId: Long,
    val intact: Boolean
)