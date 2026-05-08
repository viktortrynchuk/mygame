package com.example.mygame.dao.messaging_and_information

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.messaging_and_information.DoveEntity

@Dao
interface DoveDao : BaseDao<DoveEntity> {
    @Query("SELECT * FROM dove WHERE homeLandId = :landId")
    suspend fun byHome(landId: Long): List<DoveEntity>
}