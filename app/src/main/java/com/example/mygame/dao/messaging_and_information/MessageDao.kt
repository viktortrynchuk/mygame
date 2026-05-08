package com.example.mygame.dao.messaging_and_information

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.messaging_and_information.MessageEntity

@Dao
interface MessageDao : BaseDao<MessageEntity> {
//    @Query("SELECT * FROM message WHERE toActorId = :actorId ORDER BY id DESC")
//    suspend fun inbox(actorId: Long): List<MessageEntity>
@Query("""
        SELECT * FROM message 
        WHERE toActorId = :actorId 
           OR (toActorId IS NULL AND toRole IS NOT NULL)
        ORDER BY id ASC
    """)
suspend fun inbox(actorId: Long): List<MessageEntity>

    @Query("SELECT * FROM message WHERE fromActorId = :actorId ORDER BY id DESC")
    suspend fun outbox(actorId: Long): List<MessageEntity>

    @Query("SELECT * FROM message WHERE id = :messageId LIMIT 1")
    suspend fun byId(messageId: Long): MessageEntity?
}