package com.example.mygame.engine_and_helpers.dignity_duels_conflicts

import com.example.mygame.dao.dignity_duels_conflicts.ConflictDao
import com.example.mygame.dao.dignity_duels_conflicts.DuelDao
import com.example.mygame.dao.dignity_duels_conflicts.HonorLogDao
import com.example.mygame.database.dignity_duels_conflicts.DuelEntity
import com.example.mygame.database.dignity_duels_conflicts.HonorLogEntity
import com.example.mygame.database.dignity_duels_conflicts.PersonalConflictEntity
import javax.inject.Inject
import javax.inject.Singleton

interface DignityService {
    suspend fun issueChallenge(challengerId: Long, challengedId: Long, turn: Int): Long
    suspend fun duelsOf(nobleId: Long): List<DuelEntity>
    suspend fun recordHonor(nobleId: Long, delta: Int, reason: String, turn: Int): Long
    suspend fun conflictsOf(nobleId: Long): List<PersonalConflictEntity>
    suspend fun upsertConflict(conflict: PersonalConflictEntity): Long
    /** Finalize an existing duel with a computed outcome string (e.g., "WIN_A", "WIN_B", "DRAW"). */
    suspend fun finalizeDuel(duel: DuelEntity, outcome: String): Long
}

@Singleton
class DignityServiceImpl @Inject constructor(
    private val duelDao: DuelDao,
    private val honorDao: HonorLogDao,
    private val conflictDao: ConflictDao
) : DignityService {
    override suspend fun issueChallenge(challengerId: Long, challengedId: Long, turn: Int): Long =
        duelDao.upsert(
            DuelEntity(id = 0, participantA = challengerId, participantB = challengedId, turn = turn, outcome = "PENDING")
        )

    override suspend fun duelsOf(nobleId: Long) = duelDao.forNoble(nobleId)

    override suspend fun recordHonor(nobleId: Long, delta: Int, reason: String, turn: Int): Long =
        honorDao.upsert(HonorLogEntity(id = 0, nobleId = nobleId, delta = delta, reason = reason, turn = turn))

    override suspend fun conflictsOf(nobleId: Long) = conflictDao.involving(nobleId)

    override suspend fun upsertConflict(conflict: PersonalConflictEntity) = conflictDao.upsert(conflict)

    override suspend fun finalizeDuel(duel: DuelEntity, outcome: String): Long =
        duelDao.upsert(duel.copy(outcome = outcome))
}

//import com.example.mygame.dao.dignity_duels_conflicts.DuelDao
//import com.example.mygame.database.dignity_duels_conflicts.DuelEntity
//import javax.inject.Inject
//import javax.inject.Singleton
//
//interface DignityService {
//    /** Persist the duel with a final outcome (e.g. "WIN_A", "WIN_B", "DRAW", "AVOIDED"). */
//    suspend fun finalizeDuel(duel: DuelEntity, outcome: String)
//}
//
//@Singleton
//class DignityServiceImpl @Inject constructor(
//    private val duelDao: DuelDao
//) : DignityService {
//
//    override suspend fun finalizeDuel(duel: DuelEntity, outcome: String) {
//        // Room upsert of the duel result (same idea as before)
//        duelDao.upsert(duel.copy(outcome = outcome))
//        // If you previously adjusted honor here, inject HonorLogDao and log it the same way.
//    }
//}