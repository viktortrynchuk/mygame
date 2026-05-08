package com.example.mygame.database.foundations_core

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "rule_override",
    indices = [Index(value = ["scenarioId"], name = "idx_rule_override_scn")]
)
data class RuleOverrideEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val key: String,
    val value: String,
    val scenarioId: Long
)