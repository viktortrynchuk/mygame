package com.example.mygame.engine_and_helpers.messaging_and_information

import com.example.mygame.dao.messaging_and_information.InterceptionDao
import com.example.mygame.dao.messaging_and_information.MessageDao
import com.example.mygame.dao.messaging_and_information.SealDao
import com.example.mygame.database.messaging_and_information.InterceptionLogEntity
import com.example.mygame.database.messaging_and_information.MessageEntity
import com.example.mygame.database.messaging_and_information.MessageType
import com.example.mygame.database.messaging_and_information.SealEntity
import javax.inject.Inject
import javax.inject.Singleton

interface MessagingService {
    /** Queue a letter message. Optionally attach a seal entry. */
    suspend fun sendLetter(
        fromActorId: Long,
        toActorId: Long?,
        toRole: String?,
        payload: String,
        sentTurn: Int,
        sealed: Boolean = true
    ): Long

    /** Queue an oral message (no seal). */
    suspend fun sendOral(
        fromActorId: Long,
        toActorId: Long?,
        toRole: String?,
        payload: String,
        sentTurn: Int
    ): Long

    suspend fun inbox(actorId: Long): List<MessageEntity>
    suspend fun outbox(actorId: Long): List<MessageEntity>

    /** Mark a message as broken + re-sealed with given accuracy (0..100). */
    suspend fun breakAndReseal(original: MessageEntity, accuracy: Int): Long

    /** Log an interception attempt/result for a message. */
    suspend fun logInterception(messageId: Long, turn: Int, details: String): Long
}

@Singleton
class MessagingServiceImpl @Inject constructor(
    private val messageDao: MessageDao,
    private val sealDao: SealDao,
    private val interceptionDao: InterceptionDao
) : MessagingService {
    override suspend fun sendLetter(
        fromActorId: Long,
        toActorId: Long?,
        toRole: String?,
        payload: String,
        sentTurn: Int,
        sealed: Boolean
    ): Long {
        val id = messageDao.upsert(
            MessageEntity(
                id = 0,
                fromActorId = fromActorId,
                toActorId = toActorId,
                toRole = toRole,
                type = MessageType.LETTER,
                stampBroken = false,
                sentTurn = sentTurn,
                payload = payload
            )
        )
        if (sealed) {
            sealDao.upsert(SealEntity(id = 0, messageId = id, accuracy = 100))
        }
        return id
    }

    override suspend fun sendOral(
        fromActorId: Long,
        toActorId: Long?,
        toRole: String?,
        payload: String,
        sentTurn: Int
    ): Long = messageDao.upsert(
        MessageEntity(
            id = 0,
            fromActorId = fromActorId,
            toActorId = toActorId,
            toRole = toRole,
            type = MessageType.ORAL,
            stampBroken = false,
            sentTurn = sentTurn,
            payload = payload
        )
    )

    override suspend fun inbox(actorId: Long) = messageDao.inbox(actorId)
    override suspend fun outbox(actorId: Long) = messageDao.outbox(actorId)

    override suspend fun breakAndReseal(original: MessageEntity, accuracy: Int): Long {
        val updated = original.copy(stampBroken = true)
        val id = messageDao.upsert(updated)
        sealDao.upsert(SealEntity(id = 0, messageId = id, accuracy = accuracy.coerceIn(0, 100)))
        return id
    }

    override suspend fun logInterception(messageId: Long, turn: Int, details: String): Long =
        interceptionDao.upsert(InterceptionLogEntity(id = 0, messageId = messageId, turn = turn, details = details))
}