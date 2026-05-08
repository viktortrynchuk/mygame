package com.example.mygame.dao.roles_and_offices

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.roles_and_offices.RoleEntity

@Dao
interface RoleDao : BaseDao<RoleEntity> {
    @Query("SELECT * FROM role ORDER BY id")
    suspend fun list(): List<RoleEntity>
}