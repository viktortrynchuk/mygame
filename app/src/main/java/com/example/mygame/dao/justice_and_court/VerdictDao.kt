package com.example.mygame.dao.justice_and_court

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.justice_and_court.VerdictEntity

@Dao
interface VerdictDao : BaseDao<VerdictEntity> {
    @Query("SELECT * FROM verdict WHERE Id = :trialId")
    suspend fun forTrial(trialId: Long): VerdictEntity?
}