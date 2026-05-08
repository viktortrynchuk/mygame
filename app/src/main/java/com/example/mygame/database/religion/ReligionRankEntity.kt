package com.example.mygame.database.religion

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "religion_rank",
    indices = [Index(value = ["religionId"], name = "idx_religion_rank_rel")]
)
data class ReligionRankEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val religionId: Long,
    val orderIndex: Int,
    val title: String
)