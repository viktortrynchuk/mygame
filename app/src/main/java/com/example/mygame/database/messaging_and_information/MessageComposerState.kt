package com.example.mygame.database.messaging_and_information

import com.example.mygame.engine_and_helpers.messaging_and_information.DraftField
import com.example.mygame.engine_and_helpers.messaging_and_information.MessageDraft
import com.example.mygame.engine_and_helpers.messaging_and_information.OrderKind

data class MessageComposerState(
    val orderKind: OrderKind,
    val requiredFields: Set<DraftField>,
    val draft: MessageDraft
)