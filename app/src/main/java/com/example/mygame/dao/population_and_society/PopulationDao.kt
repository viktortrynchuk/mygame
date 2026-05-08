package com.example.mygame.dao.population_and_society

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.population_and_society.PopulationStatEntity

@Dao
interface PopulationDao : BaseDao<PopulationStatEntity> {
    @Query("SELECT * FROM population_stat WHERE landId = :landId")
    suspend fun get(landId: Long): PopulationStatEntity?
}