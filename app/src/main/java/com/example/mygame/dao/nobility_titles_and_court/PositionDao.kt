package com.example.mygame.dao.nobility_titles_and_court

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.nobility_titles_and_court.CourtPositionEntity

@Dao
interface PositionDao : BaseDao<CourtPositionEntity> {
    @Query("SELECT * FROM court_position WHERE type = :type")
    suspend fun byType(type: String): List<CourtPositionEntity>
}