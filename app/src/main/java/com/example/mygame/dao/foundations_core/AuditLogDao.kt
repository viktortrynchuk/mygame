package com.example.mygame.dao.foundations_core

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.foundations_core.AuditLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditLogDao : BaseDao<AuditLogEntity> {
    @Query("SELECT * FROM audit_log WHERE turn = :turn ORDER BY id")
    fun byTurn(turn: Int): Flow<List<AuditLogEntity>>

    @Query("SELECT * FROM audit_log ORDER BY id DESC LIMIT :n")
    fun recent(n: Int): Flow<List<AuditLogEntity>>

    @Query("""
    SELECT COUNT(*) FROM audit_log
    WHERE turn = :turn
      AND action = :action
      AND payloadJson LIKE :payloadFragment
""")
    suspend fun countByActionAndPayloadFragment(
        turn: Int,
        action: String,
        payloadFragment: String
    ): Int
}
