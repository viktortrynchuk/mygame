package com.example.mygame.dao.justice_and_court

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.justice_and_court.PunishmentEntity

@Dao
interface PunishmentDao : BaseDao<PunishmentEntity> {
    @Query("SELECT * FROM punishment WHERE verdictId = :verdictId")
    suspend fun forVerdict(verdictId: Long): List<PunishmentEntity>
}