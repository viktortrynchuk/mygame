package com.example.mygame.dao.world_and_geography

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.world_and_geography.StructureProgressEntity

@Dao
interface StructureProgressDao : BaseDao<StructureProgressEntity> {
    @Query("SELECT * FROM structure_progress WHERE structureId = :id")
    suspend fun get(id: Long): StructureProgressEntity?
}