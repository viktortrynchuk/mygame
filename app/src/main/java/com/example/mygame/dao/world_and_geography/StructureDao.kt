package com.example.mygame.dao.world_and_geography

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.world_and_geography.StructureEntity

@Dao
interface StructureDao : BaseDao<StructureEntity> {
    @Query("SELECT * FROM structure WHERE id = :structureId LIMIT 1")
    suspend fun byId(structureId: Long): StructureEntity?

    @Query("SELECT * FROM structure WHERE landId = :landId")
    suspend fun byLand(landId: Long): List<StructureEntity>

    @Query("SELECT * FROM structure")
    suspend fun getAll(): List<StructureEntity>
}