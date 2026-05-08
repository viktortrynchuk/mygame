package com.example.mygame.dao.world_and_geography

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.world_and_geography.RiverSegmentEntity

@Dao
interface RiverSegmentDao : BaseDao<RiverSegmentEntity> {
    @Query("SELECT * FROM river_segment WHERE riverId = :riverId ORDER BY orderIndex")
    suspend fun segments(riverId: Long): List<RiverSegmentEntity>
}