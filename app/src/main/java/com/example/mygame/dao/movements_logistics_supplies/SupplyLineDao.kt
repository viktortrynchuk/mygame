package com.example.mygame.dao.movements_logistics_supplies

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.movements_logistics_supplies.SupplyLineEntity

@Dao
interface SupplyLineDao : BaseDao<SupplyLineEntity> {
    @Query("SELECT * FROM supply_line WHERE armyId = :armyId")
    suspend fun forArmy(armyId: Long): SupplyLineEntity?
}