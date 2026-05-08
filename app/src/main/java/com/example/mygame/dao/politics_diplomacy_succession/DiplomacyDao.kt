package com.example.mygame.dao.politics_diplomacy_succession

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.politics_diplomacy_succession.DiplomaticStatusEntity

@Dao
interface DiplomacyDao : BaseDao<DiplomaticStatusEntity> {
    @Query("SELECT * FROM diplomatic_status WHERE a = :a AND b = :b")
    suspend fun get(a: Long, b: Long): DiplomaticStatusEntity?
}