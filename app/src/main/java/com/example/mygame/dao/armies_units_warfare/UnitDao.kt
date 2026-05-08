package com.example.mygame.dao.armies_units_warfare

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.armies_units_warfare.UnitEntity

@Dao
interface UnitDao : BaseDao<UnitEntity> {
    @Query("SELECT * FROM unit WHERE armyId = :armyId")
    suspend fun forArmy(armyId: Long): List<UnitEntity>
}