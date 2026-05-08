package com.example.mygame.dao.armies_units_warfare

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.armies_units_warfare.MilitaryOrderEntity

@Dao
interface MilitaryOrderDao : BaseDao<MilitaryOrderEntity> {
    @Query("SELECT * FROM military_order WHERE armyId = :armyId")
    suspend fun forArmy(armyId: Long): List<MilitaryOrderEntity>
}