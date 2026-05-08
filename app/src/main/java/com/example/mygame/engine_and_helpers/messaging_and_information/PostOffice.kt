package com.example.mygame.engine_and_helpers.messaging_and_information

import com.example.mygame.dao.messaging_and_information.KnowledgeDao
import com.example.mygame.dao.messaging_and_information.MessageDao
import com.example.mygame.dao.messaging_and_information.SealDao
import com.example.mygame.database.messaging_and_information.DeliveryMethod
import com.example.mygame.database.messaging_and_information.KnowledgeEntryEntity
import com.example.mygame.database.messaging_and_information.MessageEntity
import com.example.mygame.database.messaging_and_information.MessageType
import com.example.mygame.database.messaging_and_information.SealEntity
import javax.inject.Inject
import javax.inject.Singleton

interface PostOffice {
    suspend fun send(
        from: Long,
        toActorId: Long?,
        toRole: String?,
        messageType: MessageType,
        delivery: DeliveryMethod,
        body: String,
        turn: Int,
        sealed: Boolean
    ): Long

    suspend fun inbox(actorId: Long): List<MessageEntity>
    suspend fun outbox(actorId: Long): List<MessageEntity>
    suspend fun remember(actorId: Long, fact: String, sourceMsgId: Long, turn: Int): Long
}

/** Minimal post office wrapper for message IO + knowledge writes. */
@Singleton
class DaoPostOffice @Inject constructor(
    private val messageDao: MessageDao,
    private val sealDao: SealDao,
    private val knowledgeDao: KnowledgeDao
) : PostOffice {
    override suspend fun inbox(actorId: Long): List<MessageEntity> = messageDao.inbox(actorId)
    override suspend fun outbox(actorId: Long): List<MessageEntity> = messageDao.outbox(actorId)

    override suspend fun send(
        from: Long,
        toActorId: Long?,
        toRole: String?,
        messageType: MessageType,
        delivery: DeliveryMethod,
        body: String,
        turn: Int,
        sealed: Boolean
    ): Long {

        val storedMessageType = if (sealed) MessageType.SEALED_LETTER else messageType

        val id = messageDao.upsert(
            MessageEntity(
                id = 0,
                fromActorId = from,
                toActorId = toActorId,
                toRole = toRole,
                type = storedMessageType,
                stampBroken = false,
                sentTurn = turn,
                payload = body
            )
        )

        if (sealed && storedMessageType == MessageType.SEALED_LETTER) {
            sealDao.upsert(
                SealEntity(
                    id = 0,
                    messageId = id,
                    accuracy = 100
                )
            )
        }

        return id
    }

    override suspend fun remember(actorId: Long, fact: String, sourceMsgId: Long, turn: Int): Long =
        knowledgeDao.upsert(
            KnowledgeEntryEntity(id = 0, actorId = actorId, fact = fact, sourceMsgId = sourceMsgId, turn = turn, confirmed = true)
        )
}