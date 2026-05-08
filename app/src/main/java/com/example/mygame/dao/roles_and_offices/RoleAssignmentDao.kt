package com.example.mygame.dao.roles_and_offices

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.roles_and_offices.RoleAssignmentEntity

@Dao
interface RoleAssignmentDao : BaseDao<RoleAssignmentEntity> {
    @Query("SELECT * FROM role_assignment WHERE roleId = :roleId")
    suspend fun forRole(roleId: Long): List<RoleAssignmentEntity>
}