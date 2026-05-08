package com.example.mygame.engine_and_helpers.ui_facades

import com.example.mygame.engine_and_helpers.messaging_and_information.MessagingService
import javax.inject.Inject
import javax.inject.Singleton

interface MessagingFacade {
    suspend fun threadFor(actorId: Long): MessageThread
    suspend fun sendLetter(from: Long, to: Long?, toRole: String?, body: String, turn: Int): Long
    suspend fun sendOral(from: Long, to: Long?, toRole: String?, body: String, turn: Int): Long
}

@Singleton
class MessagingFacadeImpl @Inject constructor(
    private val svc: MessagingService
) : MessagingFacade {
    override suspend fun threadFor(actorId: Long) = MessageThread(svc.inbox(actorId), svc.outbox(actorId))
    override suspend fun sendLetter(from: Long, to: Long?, toRole: String?, body: String, turn: Int) =
        svc.sendLetter(from, to, toRole, body, turn)
    override suspend fun sendOral(from: Long, to: Long?, toRole: String?, body: String, turn: Int) =
        svc.sendOral(from, to, toRole, body, turn)
}