package com.example.mygame.database.politics_diplomacy_succession

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "marriage_proposal",
    indices = [Index(value = ["toNobleId"], name = "idx_marriage_to")]
)
data class MarriageProposalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fromNobleId: Long,
    val toNobleId: Long,
    val status: String
)