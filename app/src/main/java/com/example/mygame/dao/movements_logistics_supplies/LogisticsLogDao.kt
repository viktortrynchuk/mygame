package com.example.mygame.dao.movements_logistics_supplies

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.movements_logistics_supplies.LogisticsLogEntity

@Dao
interface LogisticsLogDao : BaseDao<LogisticsLogEntity> {
    @Query("SELECT * FROM logistics_log WHERE turn = :turn")
    suspend fun inTurn(turn: Int): List<LogisticsLogEntity>
}