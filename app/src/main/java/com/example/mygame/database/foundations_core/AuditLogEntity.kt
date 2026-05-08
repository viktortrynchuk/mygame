package com.example.mygame.database.foundations_core

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "audit_log",
    indices = [Index(value = ["turn"], name = "idx_audit_log_turn")]
)
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val turn: Int,
    val actorId: Long?,
    val action: String,
    val payloadJson: String,
    val hash: String,
    val createdAt: Long
)