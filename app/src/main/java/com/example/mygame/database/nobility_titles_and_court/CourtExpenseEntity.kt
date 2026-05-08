package com.example.mygame.database.nobility_titles_and_court

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "court_expense",
    indices = [Index(value = ["nobleId"], name = "idx_court_expense_noble")]
)
data class CourtExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nobleId: Long,
    val type: String,
    val amount: Int
)