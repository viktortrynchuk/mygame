package com.example.mygame.dao.rebellion_banditry

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.rebellion_banditry.OutlawEntity

@Dao
interface OutlawDao : BaseDao<OutlawEntity> {
    @Query("SELECT * FROM outlaw WHERE banditGroupId = :groupId")
    suspend fun forGroup(groupId: Long): List<OutlawEntity>
}