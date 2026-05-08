package com.example.mygame.database.religion

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "celibacy_rule")
data class CelibacyRuleEntity(
    @PrimaryKey val religionId: Long,
    val celibacy: Boolean
)