package com.example.mygame.dao.nobility_titles_and_court

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.nobility_titles_and_court.CourtMembershipEntity

@Dao
interface CourtMembershipDao : BaseDao<CourtMembershipEntity> {
    @Query("SELECT * FROM court_membership WHERE rulerId = :rulerId")
    suspend fun byRuler(rulerId: Long): List<CourtMembershipEntity>
}