package com.example.mygame.dao.movements_logistics_supplies

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.movements_logistics_supplies.MovementOrderEntity

@Dao
interface MovementOrderDao : BaseDao<MovementOrderEntity> {
    @Query("SELECT * FROM movement_order WHERE armyId = :armyId AND carrierType = 'ARMY'")
    suspend fun forArmy(armyId: Long): List<MovementOrderEntity>

    @Query("SELECT * FROM movement_order WHERE armyId = :carrierId AND carrierType = :carrierType")
    suspend fun forCarrier(carrierId: Long, carrierType: String): List<MovementOrderEntity>

    @Query("SELECT * FROM movement_order WHERE status IN ('CREATED','IN_PROGRESS','QUEUED')")
    suspend fun getActive(): List<MovementOrderEntity>

    @Query("SELECT * FROM movement_order WHERE messageId = :messageId LIMIT 1")
    suspend fun forMessage(messageId: Long): MovementOrderEntity?
}