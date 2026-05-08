package com.example.mygame.dao.rebellion_banditry

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.rebellion_banditry.BanditGroupEntity

@Dao
interface BanditGroupDao : BaseDao<BanditGroupEntity> {
    @Query("SELECT * FROM bandit_group WHERE landId = :landId")
    suspend fun inLand(landId: Long): List<BanditGroupEntity>
}