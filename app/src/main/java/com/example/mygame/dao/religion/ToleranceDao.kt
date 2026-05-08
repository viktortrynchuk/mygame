package com.example.mygame.dao.religion

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.religion.ToleranceMatrixEntity

@Dao
interface ToleranceDao : BaseDao<ToleranceMatrixEntity> {
    @Query("SELECT * FROM tolerance_matrix WHERE religionA = :a AND religionB = :b")
    suspend fun get(a: Long, b: Long): ToleranceMatrixEntity?
}