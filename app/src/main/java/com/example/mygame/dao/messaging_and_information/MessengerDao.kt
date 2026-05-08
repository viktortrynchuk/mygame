package com.example.mygame.dao.messaging_and_information

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.messaging_and_information.MessengerEntity

@Dao
interface MessengerDao : BaseDao<MessengerEntity> {
    @Query("SELECT * FROM messenger WHERE landId = :landId")
    suspend fun byLand(landId: Long): List<MessengerEntity>
}