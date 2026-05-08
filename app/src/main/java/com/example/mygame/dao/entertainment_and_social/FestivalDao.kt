package com.example.mygame.dao.entertainment_and_social

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.entertainment_and_social.FestivalEntity

@Dao
interface FestivalDao : BaseDao<FestivalEntity> {
    @Query("SELECT * FROM festival WHERE landId = :landId")
    suspend fun inLand(landId: Long): List<FestivalEntity>
}