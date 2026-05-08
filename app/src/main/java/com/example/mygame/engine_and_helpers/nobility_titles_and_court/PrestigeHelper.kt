package com.example.mygame.engine_and_helpers.nobility_titles_and_court


import com.example.mygame.dao.nobility_titles_and_court.CourtMembershipDao
import com.example.mygame.dao.nobility_titles_and_court.PositionDao

/** Helper for computing prestige changes from court size/positions. */
class PrestigeHelper(
    private val CourtMembershipDao: CourtMembershipDao,
    private val positionDao: PositionDao
) {
    suspend fun prestigeDeltaForJoiningCourt(rulerId: Long): Int {
        val size = CourtMembershipDao.byRuler(rulerId).size
        return (5 + size / 3).coerceAtMost(20)
    }

    suspend fun prestigeDeltaForPosition(type: String): Int = when (type) {
        "CHANCELLOR" -> 15
        "COIN_HOLDER" -> 10
        "DEFENSE_COMMANDER" -> 12
        else -> 5
    } + positionDao.byType(type).size / 10
}