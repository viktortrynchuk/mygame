package com.example.mygame.dao.armies_units_warfare

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.armies_units_warfare.BattleEntity

@Dao
interface BattleDao : BaseDao<BattleEntity> {
    @Query("SELECT * FROM battle WHERE turn = :turn")
    suspend fun inTurn(turn: Int): List<BattleEntity>
}