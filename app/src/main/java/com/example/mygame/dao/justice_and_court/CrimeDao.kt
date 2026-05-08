package com.example.mygame.dao.justice_and_court

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.justice_and_court.CrimeEntity

@Dao
interface CrimeDao : BaseDao<CrimeEntity> {
    @Query("SELECT * FROM crime WHERE landId = :landId")
    suspend fun inLand(landId: Long): List<CrimeEntity>
}