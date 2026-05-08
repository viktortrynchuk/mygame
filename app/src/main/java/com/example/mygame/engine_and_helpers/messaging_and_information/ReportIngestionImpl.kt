package com.example.mygame.engine_and_helpers.messaging_and_information

//import javax.inject.Inject
//import javax.inject.Singleton
//
//interface ReportIngestion {
//    /** Convert report-like letters into knowledge items; return count ingested. */
//    suspend fun ingestReportsFor(actorId: Long): Int
//}
//
//@Singleton
//class ReportIngestionImpl @Inject constructor(
//    private val post: PostOffice
//) : ReportIngestion {
//
//    override suspend fun ingestReportsFor(actorId: Long): Int {
//        val inbox = post.inbox(actorId)
//        var count = 0
//
//        for (msg in inbox) {
//            // Keep parity with previous logic: handle both LETTER and ORAL
//            if (msg.type == "LETTER" || msg.type == "ORAL") {
//                msg.payload.lineSequence()
//                    .map { it.trim() }
//                    .filter { it.isNotEmpty() }
//                    .forEach { line ->
//                        // Extract a normalized “fact” string. Replace with KnowledgeCodec if you have it.
//                        val fact: String? = extractFact(line)
//                        if (fact != null) {
//                            // Persist into knowledge table through PostOffice
//                            post.remember(
//                                actorId = actorId,
//                                fact = fact,
//                                sourceMsgId = msg.id,
//                                turn = msg.sentTurn
//                            )
//                            count++
//                        }
//                    }
//            }
//        }
//        return count
//    }
//
//    /**
//     * Minimal parser compatible with prior behavior:
//     * - Accepts lines starting with "REPORT:" and stores the remainder.
//     * - Also accepts a few system-style signals as-is (you can extend this list).
//     * - Return null for lines that are not knowledge-bearing.
//     */
//    private fun extractFact(line: String): String? {
//        if (line.isBlank()) return null
//        return when {
//            line.startsWith("REPORT:", ignoreCase = false) ->
//                line.removePrefix("REPORT:").trim().ifEmpty { null }
//
//            // Optional: treat some “signal” lines as knowledge too
//            line.startsWith("BATTLE_AT:") ||
//                    line.startsWith("DUEL_REQUEST:") ||
//                    line.startsWith("DECLARE_WAR:") ||
//                    line.startsWith("SPY_SCOUT:") -> line
//
//            else -> null
//        }
//    }
//}