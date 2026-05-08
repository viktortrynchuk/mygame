package com.example.mygame.engine_and_helpers.religion

import com.example.mygame.dao.religion.CelibacyRuleDao
import com.example.mygame.dao.religion.ConversionDao
import com.example.mygame.dao.religion.LeaderDao
import com.example.mygame.dao.religion.MonasteryDao
import com.example.mygame.dao.religion.MonkDao
import com.example.mygame.dao.religion.PriestDao
import com.example.mygame.dao.religion.RankDao
import com.example.mygame.dao.religion.ReligionDao
import com.example.mygame.dao.religion.TempleDao
import com.example.mygame.dao.religion.ToleranceDao
import com.example.mygame.database.religion.CelibacyRuleEntity
import com.example.mygame.database.religion.ConversionTaskEntity
import com.example.mygame.database.religion.LeaderEntity
import com.example.mygame.database.religion.MonasteryEntity
import com.example.mygame.database.religion.MonkEntity
import com.example.mygame.database.religion.PriestEntity
import com.example.mygame.database.religion.ReligionEntity
import com.example.mygame.database.religion.ReligionRankEntity
import com.example.mygame.database.religion.TempleEntity

interface ReligionService {
    suspend fun religions(): List<ReligionEntity>
    suspend fun ranksOf(religionId: Long): List<ReligionRankEntity>
    suspend fun priestsOf(religionId: Long): List<PriestEntity>
    suspend fun leaderOf(religionId: Long): LeaderEntity?
    suspend fun setLeader(religionId: Long, priestId: Long): Long
    suspend fun tolerance(a: Long, b: Long): Boolean
    suspend fun celibacyRule(religionId: Long): CelibacyRuleEntity?
    suspend fun conversionsFor(targetType: String, targetRef: Long): List<ConversionTaskEntity>
    suspend fun scheduleConversion(task: ConversionTaskEntity): Long
    suspend fun templesIn(landId: Long): List<TempleEntity>
    suspend fun monasteriesIn(landId: Long): List<MonasteryEntity>
    suspend fun travelingMonks(): List<MonkEntity>
}

class ReligionServiceImpl(
    private val religionDao: ReligionDao,
    private val rankDao: RankDao,
    private val priestDao: PriestDao,
    private val leaderDao: LeaderDao,
    private val toleranceDao: ToleranceDao,
    private val celibacyRuleDao: CelibacyRuleDao,
    private val conversionDao: ConversionDao,
    private val templeDao: TempleDao,
    private val monasteryDao: MonasteryDao,
    private val monkDao: MonkDao
) : ReligionService {
    override suspend fun religions() = religionDao.list()
    override suspend fun ranksOf(religionId: Long) = rankDao.ranks(religionId)
    override suspend fun priestsOf(religionId: Long) = priestDao.byReligion(religionId)
    override suspend fun leaderOf(religionId: Long) = leaderDao.get(religionId)
    override suspend fun setLeader(religionId: Long, priestId: Long): Long =
        leaderDao.upsert(LeaderEntity(religionId = religionId, priestId = priestId))

    override suspend fun tolerance(a: Long, b: Long): Boolean =
        toleranceDao.get(a, b)?.tolerant == true

    override suspend fun celibacyRule(religionId: Long) = celibacyRuleDao.get(religionId)
    override suspend fun conversionsFor(targetType: String, targetRef: Long) = conversionDao.byTarget(targetType, targetRef)
    override suspend fun scheduleConversion(task: ConversionTaskEntity) = conversionDao.upsert(task)
    override suspend fun templesIn(landId: Long) = templeDao.byLand(landId)
    override suspend fun monasteriesIn(landId: Long) = monasteryDao.byLand(landId)
    override suspend fun travelingMonks() = monkDao.traveling()
}
