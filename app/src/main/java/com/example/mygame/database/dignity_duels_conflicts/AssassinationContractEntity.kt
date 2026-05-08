package com.example.mygame.database.dignity_duels_conflicts

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assassination_contract")
data class AssassinationContractEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hirerId: Long,
    val targetId: Long,
    val status: String
)