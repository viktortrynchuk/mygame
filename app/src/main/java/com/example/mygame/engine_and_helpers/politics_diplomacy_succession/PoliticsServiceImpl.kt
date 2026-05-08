package com.example.mygame.engine_and_helpers.politics_diplomacy_succession

import com.example.mygame.dao.politics_diplomacy_succession.AmbassadorDao
import com.example.mygame.dao.politics_diplomacy_succession.CasusBelliDao
import com.example.mygame.dao.politics_diplomacy_succession.CountryDao
import com.example.mygame.dao.politics_diplomacy_succession.DiplomacyDao
import com.example.mygame.dao.politics_diplomacy_succession.MarriageDao
import com.example.mygame.dao.politics_diplomacy_succession.SuccessionDao
import com.example.mygame.database.politics_diplomacy_succession.AmbassadorEntity
import com.example.mygame.database.politics_diplomacy_succession.CasusBelliEntity
import com.example.mygame.database.politics_diplomacy_succession.CountryEntity
import com.example.mygame.database.politics_diplomacy_succession.DiplomaticStatusEntity
import com.example.mygame.database.politics_diplomacy_succession.MarriageProposalEntity
import com.example.mygame.database.politics_diplomacy_succession.SuccessionLinkEntity

interface PoliticsService {
    suspend fun countries(): List<CountryEntity>
    suspend fun diplomaticStatus(a: Long, b: Long): DiplomaticStatusEntity?
    suspend fun setDiplomaticStatus(a: Long, b: Long, status: String): Long
    suspend fun ambassadorsOf(countryId: Long): List<AmbassadorEntity>
    suspend fun queueMarriage(proposal: MarriageProposalEntity): Long
    suspend fun pendingFor(nobleId: Long): List<MarriageProposalEntity>
    suspend fun heirsOf(rulerId: Long): List<SuccessionLinkEntity>
    suspend fun addHeir(rulerId: Long, heirId: Long): Long
    suspend fun logCasusBelli(a: Long, b: Long, reason: String, turn: Int): Long
    suspend fun casusBelliBetween(a: Long, b: Long): List<CasusBelliEntity>
}

class PoliticsServiceImpl(
    private val countryDao: CountryDao,
    private val diplomacyDao: DiplomacyDao,
    private val ambassadorDao: AmbassadorDao,
    private val marriageDao: MarriageDao,
    private val successionDao: SuccessionDao,
    private val casusBelliDao: CasusBelliDao
) : PoliticsService {
    override suspend fun countries() = countryDao.list()
    override suspend fun diplomaticStatus(a: Long, b: Long) = diplomacyDao.get(a, b)
    override suspend fun setDiplomaticStatus(a: Long, b: Long, status: String): Long =
        diplomacyDao.upsert(DiplomaticStatusEntity(a = minOf(a, b), b = maxOf(a, b), status = status))
    override suspend fun ambassadorsOf(countryId: Long) = ambassadorDao.byCountry(countryId)
    override suspend fun queueMarriage(proposal: MarriageProposalEntity) = marriageDao.upsert(proposal)
    override suspend fun pendingFor(nobleId: Long) = marriageDao.pendingFor(nobleId)
    override suspend fun heirsOf(rulerId: Long) = successionDao.heirs(rulerId)
    override suspend fun addHeir(rulerId: Long, heirId: Long): Long =
        successionDao.upsert(SuccessionLinkEntity(id = 0, rulerId = rulerId, heirId = heirId))
    override suspend fun logCasusBelli(a: Long, b: Long, reason: String, turn: Int): Long =
        casusBelliDao.upsert(CasusBelliEntity(id = 0, countryA = minOf(a, b), countryB = maxOf(a, b), reason = reason, turn = turn))
    override suspend fun casusBelliBetween(a: Long, b: Long) = casusBelliDao.between(a, b)
}