package com.example.mygame.dao.nobility_titles_and_court

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.nobility_titles_and_court.PrestigeLogEntity

@Dao
interface PrestigeDao : BaseDao<PrestigeLogEntity> {
    @Query("SELECT * FROM prestige_log WHERE nobleId = :nobleId ORDER BY id DESC")
    suspend fun byNoble(nobleId: Long): List<PrestigeLogEntity>
}