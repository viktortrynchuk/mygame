package com.example.mygame.dao.entertainment_and_social

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.entertainment_and_social.PerformanceEntity

@Dao
interface PerformanceDao : BaseDao<PerformanceEntity> {
    @Query("SELECT * FROM performance WHERE festivalId = :festivalId")
    suspend fun forFestival(festivalId: Long): List<PerformanceEntity>
}