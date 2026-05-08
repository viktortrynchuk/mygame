package com.example.mygame.database.world_and_geography

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "structure_effect",
    indices = [Index(value = ["structureType"], name = "idx_structure_effect_type")]
)
data class StructureEffectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val structureType: String,
    val effectCode: String
)