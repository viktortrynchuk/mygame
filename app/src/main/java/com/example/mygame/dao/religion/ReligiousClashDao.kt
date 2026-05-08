package com.example.mygame.dao.religion

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.religion.ReligiousClashLogEntity

@Dao
interface ReligiousClashDao : BaseDao<ReligiousClashLogEntity> {
    @Query("SELECT * FROM religious_clash_log WHERE landId = :landId ORDER BY id DESC")
    suspend fun byLand(landId: Long): List<ReligiousClashLogEntity>
}