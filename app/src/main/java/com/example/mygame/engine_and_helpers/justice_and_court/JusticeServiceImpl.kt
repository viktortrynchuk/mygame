package com.example.mygame.engine_and_helpers.justice_and_court

import com.example.mygame.dao.justice_and_court.CrimeDao
import com.example.mygame.dao.justice_and_court.PunishmentDao
import com.example.mygame.dao.justice_and_court.TrialDao
import com.example.mygame.dao.justice_and_court.VerdictDao
import com.example.mygame.database.justice_and_court.CrimeEntity
import com.example.mygame.database.justice_and_court.PunishmentEntity
import com.example.mygame.database.justice_and_court.TrialEntity
import com.example.mygame.database.justice_and_court.VerdictEntity
import javax.inject.Inject
import javax.inject.Singleton

interface JusticeService {
    suspend fun reportCrime(landId: Long, type: String, reportedBy: Long?, details: String?, turn: Int): Long
    suspend fun crimesIn(landId: Long): List<CrimeEntity>

    suspend fun openTrial(crimeId: Long, judgeNobleId: Long?, startedTurn: Int): Long
    suspend fun trialFor(crimeId: Long): TrialEntity?

    suspend fun setVerdict(trialId: Long, guilty: Boolean, turn: Int): Long
    suspend fun verdictFor(trialId: Long): VerdictEntity?

    suspend fun addPunishment(verdictId: Long, type: String): Long
    suspend fun punishmentsFor(verdictId: Long): List<PunishmentEntity>
}

@Singleton
class JusticeServiceImpl @Inject constructor(
    private val crimeDao: CrimeDao,
    private val trialDao: TrialDao,
    private val verdictDao: VerdictDao,
    private val punishmentDao: PunishmentDao
) : JusticeService {
    override suspend fun reportCrime(
        landId: Long,
        type: String,
        reportedBy: Long?,
        details: String?,
        turn: Int
    ): Long =
        crimeDao.upsert(
            CrimeEntity(
                id = 0,
                landId = landId,
                type = type,
                reportedBy = reportedBy,
                details = details,
                turn = turn
            )
        )

    override suspend fun crimesIn(landId: Long) = crimeDao.inLand(landId)

    override suspend fun openTrial(crimeId: Long, judgeNobleId: Long?, startedTurn: Int): Long =
        trialDao.upsert(
            TrialEntity(
                id = 0,
                crimeId = crimeId,
                judgeNobleId = judgeNobleId,
                startedTurn = startedTurn
            )
        )

    override suspend fun trialFor(crimeId: Long) = trialDao.forCrime(crimeId)

    override suspend fun setVerdict(trialId: Long, guilty: Boolean, turn: Int): Long =
        verdictDao.upsert(
            VerdictEntity(
                Id = 0,
                caseId = trialId,
                guilty = if (guilty) true else false,
                turn = turn
            )
        )

    override suspend fun verdictFor(trialId: Long) = verdictDao.forTrial(trialId)

    override suspend fun addPunishment(verdictId: Long, type: String): Long =
        punishmentDao.upsert(PunishmentEntity(id = 0, verdictId = verdictId, type = type))

    override suspend fun punishmentsFor(verdictId: Long) = punishmentDao.forVerdict(verdictId)
}