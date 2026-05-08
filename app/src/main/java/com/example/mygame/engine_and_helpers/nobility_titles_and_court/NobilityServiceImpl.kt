package com.example.mygame.engine_and_helpers.nobility_titles_and_court

import com.example.mygame.dao.nobility_titles_and_court.CourtExpenseDao
import com.example.mygame.dao.nobility_titles_and_court.CourtMembershipDao
import com.example.mygame.dao.nobility_titles_and_court.FavoriteDao
import com.example.mygame.dao.nobility_titles_and_court.NobleDao
import com.example.mygame.dao.nobility_titles_and_court.NobleTitleDao
import com.example.mygame.dao.nobility_titles_and_court.PositionDao
import com.example.mygame.dao.nobility_titles_and_court.PrestigeDao
import com.example.mygame.dao.nobility_titles_and_court.RespectDao
import com.example.mygame.dao.nobility_titles_and_court.TitleDao
import com.example.mygame.dao.nobility_titles_and_court.TraitDao
import com.example.mygame.database.nobility_titles_and_court.CourtExpenseEntity
import com.example.mygame.database.nobility_titles_and_court.CourtMembershipEntity
import com.example.mygame.database.nobility_titles_and_court.CourtPositionEntity
import com.example.mygame.database.nobility_titles_and_court.FavoriteFlag
import com.example.mygame.database.nobility_titles_and_court.NobleEntity
import com.example.mygame.database.nobility_titles_and_court.NobleTitleEntity
import com.example.mygame.database.nobility_titles_and_court.PrestigeLogEntity
import com.example.mygame.database.nobility_titles_and_court.RespectFearEntity
import com.example.mygame.database.nobility_titles_and_court.TitleEntity
import com.example.mygame.database.nobility_titles_and_court.TraitEntity

interface NobilityService {
    suspend fun noble(id: Long): NobleEntity?
    suspend fun traitsOf(nobleId: Long): List<TraitEntity>
    suspend fun respectFear(nobleId: Long): RespectFearEntity?
    suspend fun setFavorite(nobleId: Long, favorite: Boolean)
    suspend fun titles(): List<TitleEntity>
    suspend fun titlesOf(nobleId: Long): List<NobleTitleEntity>
    suspend fun grantTitle(nobleId: Long, titleId: Long): Long
    suspend fun courtOf(rulerId: Long): List<CourtMembershipEntity>
    suspend fun expensesOf(nobleId: Long): List<CourtExpenseEntity>
    suspend fun positionsBy(type: String): List<CourtPositionEntity>
    suspend fun logPrestige(nobleId: Long, delta: Int, reason: String, turn: Int): Long
}

class NobilityServiceImpl(
    private val nobleDao: NobleDao,
    private val traitDao: TraitDao,
    private val respectDao: RespectDao,
    private val favoriteDao: FavoriteDao,
    private val titleDao: TitleDao,
    private val nobleTitleDao: NobleTitleDao,
    private val CourtMembershipDao: CourtMembershipDao,
    private val expenseDao: CourtExpenseDao,
    private val positionDao: PositionDao,
    private val prestigeDao: PrestigeDao
) : NobilityService {
    override suspend fun noble(id: Long) = nobleDao.get(id)
    override suspend fun traitsOf(nobleId: Long) = traitDao.byNoble(nobleId)
    override suspend fun respectFear(nobleId: Long) = respectDao.get(nobleId)
    override suspend fun setFavorite(nobleId: Long, favorite: Boolean) {
        favoriteDao.upsert(FavoriteFlag(nobleId = nobleId, isFavorite = if (favorite) true else false))
    }
    override suspend fun titles() = titleDao.list()
    override suspend fun titlesOf(nobleId: Long) = nobleTitleDao.byNoble(nobleId)
    override suspend fun grantTitle(nobleId: Long, titleId: Long): Long =
        nobleTitleDao.upsert(NobleTitleEntity(nobleId = nobleId, titleId = titleId))
    override suspend fun courtOf(rulerId: Long) = CourtMembershipDao.byRuler(rulerId)
    override suspend fun expensesOf(nobleId: Long) = expenseDao.byNoble(nobleId)
    override suspend fun positionsBy(type: String) = positionDao.byType(type)
    override suspend fun logPrestige(nobleId: Long, delta: Int, reason: String, turn: Int): Long =
        prestigeDao.upsert(PrestigeLogEntity(id = 0, nobleId = nobleId, delta = delta, reason = reason, turn = turn))
}
