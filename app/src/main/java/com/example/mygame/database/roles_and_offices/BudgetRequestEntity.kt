package com.example.mygame.database.roles_and_offices

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_request")
data class BudgetRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val office: String,
    val requesterId: Long,
    val amount: Int,
    val reason: String,
    val turn: Int
)