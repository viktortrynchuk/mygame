package com.example.mygame.dao.nobility_titles_and_court

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.nobility_titles_and_court.NobleTitleEntity

@Dao
interface NobleTitleDao : BaseDao<NobleTitleEntity> {
    @Query("SELECT * FROM noble_title WHERE nobleId = :nobleId")
    suspend fun byNoble(nobleId: Long): List<NobleTitleEntity>
}