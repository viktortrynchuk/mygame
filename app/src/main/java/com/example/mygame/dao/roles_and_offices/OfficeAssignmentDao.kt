package com.example.mygame.dao.roles_and_offices

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.roles_and_offices.OfficeAssignmentEntity

@Dao
interface OfficeAssignmentDao : BaseDao<OfficeAssignmentEntity> {
    @Query("SELECT * FROM office_assignment WHERE officeId = :officeId")
    suspend fun forOffice(officeId: Long): List<OfficeAssignmentEntity>
}