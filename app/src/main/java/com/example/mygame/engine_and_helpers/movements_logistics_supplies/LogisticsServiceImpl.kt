package com.example.mygame.engine_and_helpers.movements_logistics_supplies

import com.example.mygame.dao.movements_logistics_supplies.LogisticsLogDao
import com.example.mygame.dao.movements_logistics_supplies.MovementOrderDao
import com.example.mygame.dao.movements_logistics_supplies.PathSegmentDao
import com.example.mygame.dao.movements_logistics_supplies.SupplyDepotDao
import com.example.mygame.dao.movements_logistics_supplies.SupplyLineDao
import com.example.mygame.database.movements_logistics_supplies.LogisticsLogEntity
import com.example.mygame.database.movements_logistics_supplies.MovementOrderEntity
import com.example.mygame.database.movements_logistics_supplies.PathSegmentEntity
import com.example.mygame.database.movements_logistics_supplies.SupplyDepotEntity
import com.example.mygame.database.movements_logistics_supplies.SupplyLineEntity

interface LogisticsService {
    suspend fun createMovementOrder(armyId: Long, createdTurn: Int, status: String = "QUEUED"): Long
    suspend fun attachPath(orderId: Long, pathLandIds: List<Long>): List<Long>
    suspend fun ordersForArmy(armyId: Long): List<MovementOrderEntity>

    suspend fun defineSupplyLine(armyId: Long, sourceLandId: Long, active: Boolean = true): Long
    suspend fun supplyLineOf(armyId: Long): SupplyLineEntity?

    suspend fun createDepot(landId: Long, capacity: Int): Long
    suspend fun depotsIn(landId: Long): List<SupplyDepotEntity>

    suspend fun log(turn: Int, armyId: Long?, details: String): Long
}

class LogisticsServiceImpl(
    private val movementOrderDao: MovementOrderDao,
    private val pathDao: PathSegmentDao,
    private val supplyLineDao: SupplyLineDao,
    private val depotDao: SupplyDepotDao,
    private val logDao: LogisticsLogDao
) : LogisticsService {
    override suspend fun createMovementOrder(armyId: Long, createdTurn: Int, status: String): Long =
        movementOrderDao.upsert(
            MovementOrderEntity(id = 0, armyId = armyId, createdTurn = createdTurn, status = status)
        )

    override suspend fun attachPath(orderId: Long, pathLandIds: List<Long>): List<Long> {
        val segments = pathLandIds.zipWithNext().mapIndexed { idx, (from, to) ->
            PathSegmentEntity(id = 0, movementOrderId = orderId, stepIndex = idx, fromLandId = from, toLandId = to)
        }
        return pathDao.upsertAll(segments)
    }

    override suspend fun ordersForArmy(armyId: Long) = movementOrderDao.forArmy(armyId)

    override suspend fun defineSupplyLine(armyId: Long, sourceLandId: Long, active: Boolean): Long =
        supplyLineDao.upsert(
            SupplyLineEntity(id = 0, armyId = armyId, sourceLandId = sourceLandId, active = if (active) true else false)
        )

    override suspend fun supplyLineOf(armyId: Long) = supplyLineDao.forArmy(armyId)

    override suspend fun createDepot(landId: Long, capacity: Int): Long =
        depotDao.upsert(SupplyDepotEntity(id = 0, landId = landId, capacity = capacity))

    override suspend fun depotsIn(landId: Long) = depotDao.inLand(landId)

    override suspend fun log(turn: Int, armyId: Long?, details: String): Long =
        logDao.upsert(LogisticsLogEntity(id = 0, turn = turn, armyId = armyId, details = details))
}