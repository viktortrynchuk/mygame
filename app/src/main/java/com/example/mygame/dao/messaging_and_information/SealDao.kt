package com.example.mygame.dao.messaging_and_information

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.messaging_and_information.SealEntity

@Dao
interface SealDao : BaseDao<SealEntity> {
    @Query("SELECT * FROM seal WHERE messageId = :messageId")
    suspend fun byMessage(messageId: Long): List<SealEntity>
}