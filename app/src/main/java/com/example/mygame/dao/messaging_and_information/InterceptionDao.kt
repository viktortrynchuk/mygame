package com.example.mygame.dao.messaging_and_information

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.messaging_and_information.InterceptionLogEntity

@Dao
interface InterceptionDao : BaseDao<InterceptionLogEntity> {
    @Query("SELECT * FROM interception_log WHERE messageId = :messageId")
    suspend fun byMessage(messageId: Long): List<InterceptionLogEntity>
}