package com.example.mygame.engine_and_helpers.roles_and_offices

import com.example.mygame.dao.roles_and_offices.OfficeAssignmentDao
import com.example.mygame.dao.roles_and_offices.OfficeDao
import com.example.mygame.dao.roles_and_offices.RoleAssignmentDao
import com.example.mygame.dao.roles_and_offices.RoleDao
import com.example.mygame.database.roles_and_offices.OfficeAssignmentEntity
import com.example.mygame.database.roles_and_offices.OfficeEntity
import com.example.mygame.database.roles_and_offices.RoleAssignmentEntity
import com.example.mygame.database.roles_and_offices.RoleEntity

interface OfficesService {
    suspend fun offices(): List<OfficeEntity>
    suspend fun assignOffice(officeId: Long, nobleId: Long, startTurn: Int): Long
    suspend fun assignmentsOf(officeId: Long): List<OfficeAssignmentEntity>

    suspend fun roles(): List<RoleEntity>
    suspend fun assignRole(roleId: Long, nobleId: Long, startTurn: Int): Long
    suspend fun assignmentsForRole(roleId: Long): List<RoleAssignmentEntity>
}

class OfficesServiceImpl(
    private val officeDao: OfficeDao,
    private val officeAssignDao: OfficeAssignmentDao,
    private val roleDao: RoleDao,
    private val roleAssignDao: RoleAssignmentDao
) : OfficesService {
    override suspend fun offices() = officeDao.list()

    override suspend fun assignOffice(officeId: Long, nobleId: Long, startTurn: Int): Long =
        officeAssignDao.upsert(
            OfficeAssignmentEntity(id = 0, officeId = officeId, nobleId = nobleId, startTurn = startTurn, endTurn = null)
        )

    override suspend fun assignmentsOf(officeId: Long) = officeAssignDao.forOffice(officeId)

    override suspend fun roles() = roleDao.list()

    override suspend fun assignRole(roleId: Long, nobleId: Long, startTurn: Int): Long =
        roleAssignDao.upsert(RoleAssignmentEntity(id = 0, roleId = roleId, nobleId = nobleId, startTurn = startTurn, endTurn = null))

    override suspend fun assignmentsForRole(roleId: Long) = roleAssignDao.forRole(roleId)
}