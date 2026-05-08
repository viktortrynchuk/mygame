package com.example.mygame.dao.messaging_and_information

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.messaging_and_information.KnowledgeEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KnowledgeDao : BaseDao<KnowledgeEntryEntity> {
    @Query("SELECT * FROM knowledge_entry WHERE actorId = :actorId ORDER BY turn")
    fun facts(actorId: Long): Flow<List<KnowledgeEntryEntity>>

    @Query("SELECT * FROM knowledge_entry WHERE actorId = :actorId ORDER BY id ASC")
    suspend fun byActor(actorId: Long): List<KnowledgeEntryEntity>

    @Query("SELECT * FROM knowledge_entry WHERE actorId = :actorId")
    suspend fun getAllForActor(actorId: Long): List<KnowledgeEntryEntity>

    // If you have a 'truth' flag somewhere else, adapt this.
    // In your schema KnowledgeEntryEntity has no boolean, so "true/false" must be encoded in fact or in a separate table.
    // For now return all; you can filter in Kotlin by prefix "TRUE:" etc.
    suspend fun getTrueKnowledgeForActor(actorId: Long): List<KnowledgeEntryEntity> = getAllForActor(actorId)

    @Query("""
        SELECT * FROM knowledge_entry
        WHERE actorId = :actorId
          AND fact LIKE :prefix || '%'
        ORDER BY turn DESC
    """)
    suspend fun factsByPrefix(actorId: Long, prefix: String): List<KnowledgeEntryEntity>
}