package com.example.mygame.dao.economy_resources_trade

import com.example.mygame.database.economy_resources_trade.CraftOrderEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mygame.dao.BaseDao


@Dao
interface CraftOrderDao : BaseDao<CraftOrderEntity> {

    @Query("DELETE FROM craft_order")
    suspend fun deleteAll()
}