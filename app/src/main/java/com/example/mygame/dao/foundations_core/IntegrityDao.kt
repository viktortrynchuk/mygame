package com.example.mygame.dao.foundations_core

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.foundations_core.IntegrityViolationEntity

@Dao
interface IntegrityDao : BaseDao<IntegrityViolationEntity> {
    @Query("SELECT * FROM integrity_violation WHERE turn = :turn ORDER BY id")
    suspend fun byTurn(turn: Int): List<IntegrityViolationEntity>
}
