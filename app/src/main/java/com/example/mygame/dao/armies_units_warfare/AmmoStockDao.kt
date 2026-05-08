package com.example.mygame.dao.armies_units_warfare

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.armies_units_warfare.AmmoStockEntity

@Dao
interface AmmoStockDao : BaseDao<AmmoStockEntity> {
    @Query("DELETE FROM ammo_stock")
    suspend fun deleteAll()
}