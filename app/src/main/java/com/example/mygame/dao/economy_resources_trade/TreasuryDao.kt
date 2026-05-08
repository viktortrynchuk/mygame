package com.example.mygame.dao.economy_resources_trade

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.economy_resources_trade.TreasuryEntity

@Dao
interface TreasuryDao : BaseDao<TreasuryEntity> {
    @Query("DELETE FROM treasury")
    suspend fun deleteAll()

    @Query("SELECT * FROM treasury WHERE ownerType = :ownerType AND ownerRef = :ownerRef LIMIT 1")
    suspend fun byOwner(ownerType: String, ownerRef: Long): TreasuryEntity?
}