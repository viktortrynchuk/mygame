package com.example.mygame.engine_and_helpers.ui_facades

import com.example.mygame.database.messaging_and_information.MessageEntity

// ---- Messaging Facade ----

data class MessageThread(
    val inbox: List<MessageEntity>,
    val outbox: List<MessageEntity>
)