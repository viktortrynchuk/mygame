package com.example.mygame.engine_and_helpers.messaging_and_information

import com.example.mygame.database.messaging_and_information.MessageEntity
import com.example.mygame.database.messaging_and_information.MessageType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tiny ingestion: scan inbox messages, for any line that decodes as a Fact,
 * write a KnowledgeEntry linked to that message.
 */

interface ReportIngestion {
    /** Convert report-like letters into knowledge items; return count ingested. */
    suspend fun ingestReportsFor(actorId: Long): Int
}

// Keep it simple for now: count REPORT: messages from the inbox
@Singleton
class ReportIngestionImpl @Inject constructor(
    private val post: PostOffice
) : ReportIngestion {
    override suspend fun ingestReportsFor(actorId: Long): Int {
        val inbox = post.inbox(actorId)
        var count = 0
        for (msg in inbox) {
            // Accept only letters marked as REPORT or system lines starting with known keys
            if (msg.type == MessageType.LETTER || msg.type == MessageType.ORAL) {
                val lines = msg.payload.lines()
                for (line in lines) {
                    val clean = line.trim()
                    val fact = when {
                        clean.startsWith("REPORT:") -> KnowledgeCodec.parse(clean.removePrefix("REPORT:"))
                        else -> KnowledgeCodec.parse(clean)
                    }
                    if (fact != null) {
                        post.remember(actorId, KnowledgeCodec.encode(fact), msg.id, msg.sentTurn)
                        count++
                    }
                }
            }
        }
        return count
    }
}