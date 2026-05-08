package com.example.mygame.database.movements_logistics_supplies

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "escort_assignment")
data class EscortAssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val convoyId: Long,
    val unitId: Long
)