package com.example.mygame.engine_and_helpers.ui_facades

import com.example.mygame.dao.armies_units_warfare.ArmyDao
import com.example.mygame.dao.armies_units_warfare.MilitaryOrderDao
import com.example.mygame.dao.armies_units_warfare.UnitDao
import com.example.mygame.dao.movements_logistics_supplies.SupplyDepotDao
import com.example.mygame.dao.movements_logistics_supplies.SupplyLineDao
import javax.inject.Inject
import javax.inject.Singleton

interface ArmyFacade {
    suspend fun overview(armyId: Long): ArmyOverview?
}

@Singleton
class ArmyFacadeImpl @Inject constructor(
    private val armyDao: ArmyDao,
    private val unitDao: UnitDao,
    private val orderDao: MilitaryOrderDao,
    private val supplyDao: SupplyLineDao,
    private val depotDao: SupplyDepotDao
) : ArmyFacade {
    override suspend fun overview(armyId: Long): ArmyOverview? {
        val army = armyDao.get(armyId) ?: return null
        val units = unitDao.forArmy(armyId)
        val orders = orderDao.forArmy(armyId)
        val supply = supplyDao.forArmy(armyId)
        val depots = army.landId.let { depotDao.inLand(it) }
        return ArmyOverview(army, units, orders, supply, depots)
    }
}