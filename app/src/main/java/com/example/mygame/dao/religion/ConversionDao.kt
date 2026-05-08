package com.example.mygame.dao.religion

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.religion.ConversionTaskEntity

@Dao
interface ConversionDao : BaseDao<ConversionTaskEntity> {
    @Query("SELECT * FROM conversion_task WHERE targetType = :type AND targetRef = :ref")
    suspend fun byTarget(type: String, ref: Long): List<ConversionTaskEntity>
}