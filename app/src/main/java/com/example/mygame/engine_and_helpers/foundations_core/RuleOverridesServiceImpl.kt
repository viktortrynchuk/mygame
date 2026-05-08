package com.example.mygame.engine_and_helpers.foundations_core

import com.example.mygame.dao.foundations_core.RuleOverrideDao
import com.example.mygame.database.foundations_core.RuleOverrideEntity

interface RuleOverridesService {
    suspend fun get(key: String, scenarioId: Long): RuleOverrideEntity?
    suspend fun allForScenario(scenarioId: Long): List<RuleOverrideEntity>
}

class RuleOverridesServiceImpl(
    private val dao: RuleOverrideDao
) : RuleOverridesService {
    override suspend fun get(key: String, scenarioId: Long) = dao.getByKey(key, scenarioId)
    override suspend fun allForScenario(scenarioId: Long) = dao.getAll(scenarioId)
}
