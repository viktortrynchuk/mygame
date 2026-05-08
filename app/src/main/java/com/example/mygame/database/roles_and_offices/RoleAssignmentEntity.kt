package com.example.mygame.database.roles_and_offices

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "role_assignment")
data class RoleAssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roleId: Long,
    val nobleId: Long,
    val startTurn: Int,
    val endTurn: Int?
)