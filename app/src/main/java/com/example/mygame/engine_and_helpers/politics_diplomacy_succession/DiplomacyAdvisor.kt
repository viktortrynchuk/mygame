package com.example.mygame.engine_and_helpers.politics_diplomacy_succession

import com.example.mygame.dao.politics_diplomacy_succession.AmbassadorDao
import com.example.mygame.dao.politics_diplomacy_succession.DiplomacyDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Heuristics helper for choosing the next best diplomatic action based on current status.
 */
class DiplomacyAdvisor(
    private val diplomacyDao: DiplomacyDao,
    private val ambassadorDao: AmbassadorDao
) {
    suspend fun recommend(a: Long, b: Long): String = withContext(Dispatchers.Default) {
        val status = diplomacyDao.get(a, b)?.status ?: "NEUTRAL"
        val hasAmb = ambassadorDao.byCountry(a).any { it.landId > 0 } && ambassadorDao.byCountry(b).any { it.landId > 0 }
        when (status) {
            "WAR" -> if (hasAmb) "SUE_FOR_PEACE" else "APPOINT_AMBASSADOR"
            "ALLY" -> "KEEP_TREATY"
            "TRUCE" -> "NEGOTIATE_TRADE"
            else -> if (hasAmb) "SIGN_NON_AGGRESSION_PACT" else "APPOINT_AMBASSADOR"
        }
    }
}