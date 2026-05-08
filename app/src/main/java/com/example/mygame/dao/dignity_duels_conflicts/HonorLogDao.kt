package com.example.mygame.dao.dignity_duels_conflicts

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.dignity_duels_conflicts.HonorLogEntity

@Dao
interface HonorLogDao : BaseDao<HonorLogEntity> {
    @Query("SELECT * FROM honor_log WHERE nobleId = :nobleId")
    suspend fun forNoble(nobleId: Long): List<HonorLogEntity>
}