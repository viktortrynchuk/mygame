package com.example.mygame.engine_and_helpers.entertainment_and_social

import com.example.mygame.dao.entertainment_and_social.FestivalDao
import com.example.mygame.dao.entertainment_and_social.PerformanceDao
import com.example.mygame.dao.entertainment_and_social.TournamentDao
import com.example.mygame.database.entertainment_and_social.FestivalEntity
import com.example.mygame.database.entertainment_and_social.PerformanceEntity
import com.example.mygame.database.entertainment_and_social.TournamentEntity
import javax.inject.Inject
import javax.inject.Singleton

interface SocialService {
    suspend fun scheduleFestival(landId: Long, turn: Int, theme: String?): Long
    suspend fun listFestivals(landId: Long): List<FestivalEntity>
    suspend fun scheduleTournament(landId: Long, turn: Int, prize: String?): Long
    suspend fun listTournaments(landId: Long): List<TournamentEntity>
    suspend fun addPerformance(festivalId: Long, troupe: String, rating: Int?): Long
    suspend fun performancesOf(festivalId: Long): List<PerformanceEntity>
}

@Singleton
class SocialServiceImpl @Inject constructor(
    private val festivalDao: FestivalDao,
    private val tournamentDao: TournamentDao,
    private val performanceDao: PerformanceDao
) : SocialService {
    override suspend fun scheduleFestival(landId: Long, turn: Int, theme: String?): Long =
        festivalDao.upsert(FestivalEntity(id = 0, landId = landId, turn = turn, theme = theme))

    override suspend fun listFestivals(landId: Long) = festivalDao.inLand(landId)

    override suspend fun scheduleTournament(landId: Long, turn: Int, prize: String?): Long =
        tournamentDao.upsert(TournamentEntity(id = 0, landId = landId, turn = turn, prize = prize))

    override suspend fun listTournaments(landId: Long) = tournamentDao.inLand(landId)

    override suspend fun addPerformance(festivalId: Long, troupe: String, rating: Int?): Long =
        performanceDao.upsert(PerformanceEntity(id = 0, festivalId = festivalId, troupe = troupe, rating = rating))

    override suspend fun performancesOf(festivalId: Long) = performanceDao.forFestival(festivalId)

}