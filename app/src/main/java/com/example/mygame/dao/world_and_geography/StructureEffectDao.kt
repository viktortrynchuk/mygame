package com.example.mygame.dao.world_and_geography

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.world_and_geography.StructureEffectEntity

@Dao
interface StructureEffectDao : BaseDao<StructureEffectEntity> {
    @Query("SELECT * FROM structure_effect WHERE structureType = :type")
    suspend fun byType(type: String): List<StructureEffectEntity>
}