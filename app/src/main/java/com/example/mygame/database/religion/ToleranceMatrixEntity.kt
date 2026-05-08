package com.example.mygame.database.religion

import androidx.room.Entity

@Entity(
    tableName = "tolerance_matrix",
    primaryKeys = ["religionA", "religionB"]
)
data class ToleranceMatrixEntity(
    val religionA: Long,
    val religionB: Long,
    val tolerant: Boolean
)