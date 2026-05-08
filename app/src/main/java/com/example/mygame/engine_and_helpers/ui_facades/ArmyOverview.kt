package com.example.mygame.engine_and_helpers.ui_facades

import com.example.mygame.database.armies_units_warfare.ArmyEntity
import com.example.mygame.database.armies_units_warfare.MilitaryOrderEntity
import com.example.mygame.database.armies_units_warfare.UnitEntity
import com.example.mygame.database.movements_logistics_supplies.SupplyDepotEntity
import com.example.mygame.database.movements_logistics_supplies.SupplyLineEntity

data class ArmyOverview(
    val army: ArmyEntity,
    val units: List<UnitEntity>,
    val orders: List<MilitaryOrderEntity>,
    val supply: SupplyLineEntity?,
    val depots: List<SupplyDepotEntity>
)