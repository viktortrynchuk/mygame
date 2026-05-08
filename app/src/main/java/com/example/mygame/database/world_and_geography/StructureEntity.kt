package com.example.mygame.database.world_and_geography

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "structure",
    indices = [
        Index(value = ["landId"], name = "idx_structure_land"),
        Index(value = ["ownerType", "ownerRef"], name = "idx_structure_owner")
    ]
)
data class StructureEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val structureType: String,
    val structureGroup: String,
    val ownerType: String? = null,
    val ownerRef: Long? = null
)