package com.example.mygame.dao.justice_and_court

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.justice_and_court.TrialEntity

@Dao
interface TrialDao : BaseDao<TrialEntity> {
    @Query("SELECT * FROM trial WHERE crimeId = :crimeId")
    suspend fun forCrime(crimeId: Long): TrialEntity?
}