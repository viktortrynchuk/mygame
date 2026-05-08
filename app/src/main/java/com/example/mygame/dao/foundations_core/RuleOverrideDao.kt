package com.example.mygame.dao.foundations_core

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.foundations_core.RuleOverrideEntity

@Dao
interface RuleOverrideDao : BaseDao<RuleOverrideEntity> {
    @Query("SELECT * FROM rule_override WHERE scenarioId = :scenarioId")
    suspend fun getAll(scenarioId: Long): List<RuleOverrideEntity>

    @Query("SELECT * FROM rule_override WHERE 'key' = :key AND scenarioId = :scenarioId LIMIT 1")
    suspend fun getByKey(key: String, scenarioId: Long): RuleOverrideEntity?
}