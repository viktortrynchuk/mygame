package com.example.mygame.dao.nobility_titles_and_court

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.nobility_titles_and_court.CourtExpenseEntity

@Dao
interface CourtExpenseDao : BaseDao<CourtExpenseEntity> {
    @Query("SELECT * FROM court_expense WHERE nobleId = :nobleId")
    suspend fun byNoble(nobleId: Long): List<CourtExpenseEntity>
}