package com.example.mygame.dao.armies_units_warfare

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.armies_units_warfare.EquipmentStockEntity

@Dao
interface EquipmentStockDao : BaseDao<EquipmentStockEntity> {
    @Query("DELETE FROM equipment_stock")
    suspend fun deleteAll()
}