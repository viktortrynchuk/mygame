package com.example.mygame.dao.rebellion_banditry

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.rebellion_banditry.SuppressionLogEntity

@Dao
interface SuppressionLogDao : BaseDao<SuppressionLogEntity> {
    @Query("SELECT * FROM suppression_log WHERE turn = :turn")
    suspend fun inTurn(turn: Int): List<SuppressionLogEntity>
}