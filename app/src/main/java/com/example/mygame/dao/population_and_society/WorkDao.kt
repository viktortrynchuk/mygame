package com.example.mygame.dao.population_and_society

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.population_and_society.WorkAssignmentEntity

@Dao
interface WorkDao : BaseDao<WorkAssignmentEntity> {
    @Query("SELECT * FROM work_assignment WHERE landId = :landId")
    suspend fun byLand(landId: Long): List<WorkAssignmentEntity>
}