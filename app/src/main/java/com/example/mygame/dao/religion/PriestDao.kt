package com.example.mygame.dao.religion

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.religion.PriestEntity

@Dao
interface PriestDao : BaseDao<PriestEntity> {
    @Query("SELECT * FROM priest WHERE religionId = :religionId")
    suspend fun byReligion(religionId: Long): List<PriestEntity>
}