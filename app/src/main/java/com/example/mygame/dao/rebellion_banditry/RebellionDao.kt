package com.example.mygame.dao.rebellion_banditry

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.rebellion_banditry.RebellionEntity

@Dao
interface RebellionDao : BaseDao<RebellionEntity> {
    @Query("SELECT * FROM rebellion WHERE landId = :landId")
    suspend fun inLand(landId: Long): RebellionEntity?
}