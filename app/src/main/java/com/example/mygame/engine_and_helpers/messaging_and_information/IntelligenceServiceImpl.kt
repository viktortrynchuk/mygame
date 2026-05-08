package com.example.mygame.engine_and_helpers.messaging_and_information

import com.example.mygame.dao.messaging_and_information.KnowledgeDao
import com.example.mygame.database.messaging_and_information.KnowledgeEntryEntity
import kotlinx.coroutines.flow.Flow

interface IntelligenceService {
    /** Add a fact to actor's personal knowledge base. */
    suspend fun recordFact(actorId: Long, fact: String, sourceMsgId: Long, turn: Int, confirmed: Boolean): Long
    /** Observe facts as a stream (e.g., for UI). */
    fun facts(actorId: Long): Flow<List<KnowledgeEntryEntity>>
}

class IntelligenceServiceImpl(
    private val knowledgeDao: KnowledgeDao
) : IntelligenceService {
    override suspend fun recordFact(actorId: Long, fact: String, sourceMsgId: Long, turn: Int, confirmed: Boolean): Long =
        knowledgeDao.upsert(
            KnowledgeEntryEntity(id = 0, actorId = actorId, fact = fact, sourceMsgId = sourceMsgId, turn = turn, confirmed = confirmed)
        )

    override fun facts(actorId: Long): Flow<List<KnowledgeEntryEntity>> = knowledgeDao.facts(actorId)
}