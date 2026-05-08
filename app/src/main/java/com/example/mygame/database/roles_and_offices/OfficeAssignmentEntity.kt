package com.example.mygame.database.roles_and_offices

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "office_assignment")
data class OfficeAssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val officeId: Long,
    val nobleId: Long,
    val startTurn: Int,
    val endTurn: Int?
)