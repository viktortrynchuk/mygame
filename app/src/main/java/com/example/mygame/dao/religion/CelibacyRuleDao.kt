package com.example.mygame.dao.religion

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.religion.CelibacyRuleEntity

@Dao
interface CelibacyRuleDao : BaseDao<CelibacyRuleEntity> {
    @Query("SELECT * FROM celibacy_rule WHERE religionId = :religionId")
    suspend fun get(religionId: Long): CelibacyRuleEntity?
}