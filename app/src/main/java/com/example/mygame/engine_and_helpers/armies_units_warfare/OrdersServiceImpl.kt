package com.example.mygame.engine_and_helpers.armies_units_warfare

import com.example.mygame.dao.armies_units_warfare.MilitaryOrderDao
import com.example.mygame.database.armies_units_warfare.MilitaryOrderEntity

interface OrdersService {
    suspend fun ordersFor(armyId: Long): List<MilitaryOrderEntity>
    suspend fun issueOrder(armyId: Long, type: String, payload: String, issuedTurn: Int): Long
}

class OrdersServiceImpl(
    private val orderDao: MilitaryOrderDao
) : OrdersService {
    override suspend fun ordersFor(armyId: Long) = orderDao.forArmy(armyId)
    override suspend fun issueOrder(armyId: Long, type: String, payload: String, issuedTurn: Int): Long =
        orderDao.upsert(MilitaryOrderEntity(id = 0, armyId = armyId, type = type, payload = payload, issuedTurn = issuedTurn))
}