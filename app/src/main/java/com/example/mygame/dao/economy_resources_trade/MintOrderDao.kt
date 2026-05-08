package com.example.mygame.dao.economy_resources_trade

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.economy_resources_trade.MintOrderEntity

@Dao
interface MintOrderDao : BaseDao<MintOrderEntity> {

    @Query("DELETE FROM mint_order")
    suspend fun deleteAll()
}