package com.example.mygame.dao.religion

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.religion.ReligionRankEntity

@Dao
interface RankDao : BaseDao<ReligionRankEntity> {
    @Query("SELECT * FROM religion_rank WHERE religionId = :religionId ORDER BY orderIndex")
    suspend fun ranks(religionId: Long): List<ReligionRankEntity>
}