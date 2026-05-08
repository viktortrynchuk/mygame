package com.example.mygame.dao.religion

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.religion.ReligionEntity

@Dao
interface ReligionDao : BaseDao<ReligionEntity> {
    @Query("SELECT * FROM religion ORDER BY id")
    suspend fun list(): List<ReligionEntity>
}