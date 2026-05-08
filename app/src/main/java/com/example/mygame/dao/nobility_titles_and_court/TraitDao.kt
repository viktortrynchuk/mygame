package com.example.mygame.dao.nobility_titles_and_court

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.nobility_titles_and_court.TraitEntity

@Dao
interface TraitDao : BaseDao<TraitEntity> {
    @Query("SELECT * FROM trait WHERE nobleId = :nobleId")
    suspend fun byNoble(nobleId: Long): List<TraitEntity>
}