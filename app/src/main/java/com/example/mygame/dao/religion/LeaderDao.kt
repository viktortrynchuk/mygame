package com.example.mygame.dao.religion

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.religion.LeaderEntity

@Dao
interface LeaderDao : BaseDao<LeaderEntity> {
    @Query("SELECT * FROM religion_leader WHERE religionId = :religionId")
    suspend fun get(religionId: Long): LeaderEntity?
}