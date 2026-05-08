package com.example.mygame.database.movements_logistics_supplies

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ration_plan")
data class RationPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val foodTurns: Int,
    val waterTurns: Int
)