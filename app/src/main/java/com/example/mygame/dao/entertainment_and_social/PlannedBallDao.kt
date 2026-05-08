package com.example.mygame.dao.entertainment_and_social

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.entertainment_and_social.PlannedBallAlcoholEntity
import com.example.mygame.database.entertainment_and_social.PlannedBallBardEntity
import com.example.mygame.database.entertainment_and_social.PlannedBallEntity
import com.example.mygame.database.entertainment_and_social.PlannedBallGuestEntity

@Dao
interface PlannedBallDao : BaseDao<PlannedBallEntity> {
    @Query(
        "SELECT * FROM planned_ball " +
                "WHERE organizerActorId = :actorId AND status IN ('PLANNING','READY','ORDERED_TO_CHANCELLOR') " +
                "ORDER BY eventTurn"
    )
    suspend fun activeForOrganizer(actorId: Long): List<PlannedBallEntity>

    @Query(
        "SELECT * FROM planned_ball " +
                "WHERE landId = :landId AND status IN ('PLANNING','READY','ORDERED_TO_CHANCELLOR') " +
                "ORDER BY id DESC"
    )
    suspend fun activeForLandCandidates(landId: Long): List<PlannedBallEntity>

    @Query(
        "SELECT * FROM planned_ball " +
                "WHERE landId = :landId AND status IN ('PLANNING','READY','ORDERED_TO_CHANCELLOR') " +
                "ORDER BY eventTurn LIMIT 1"
    )
    suspend fun activeForLand(landId: Long): PlannedBallEntity?

    @Query("SELECT * FROM planned_ball WHERE id = :id")
    suspend fun byId(id: Long): PlannedBallEntity?
}

@Dao
interface PlannedBallBardDao : BaseDao<PlannedBallBardEntity> {
    @Query("SELECT * FROM planned_ball_bard WHERE plannedBallId = :plannedBallId")
    suspend fun forBall(plannedBallId: Long): List<PlannedBallBardEntity>

    @Query("DELETE FROM planned_ball_bard WHERE plannedBallId = :ballId")
    suspend fun deleteForBall(ballId: Long)
}

@Dao
interface PlannedBallGuestDao : BaseDao<PlannedBallGuestEntity> {
    @Query("SELECT * FROM planned_ball_guest WHERE plannedBallId = :plannedBallId")
    suspend fun forBall(plannedBallId: Long): List<PlannedBallGuestEntity>

    @Query("DELETE FROM planned_ball_guest WHERE plannedBallId = :ballId")
    suspend fun deleteForBall(ballId: Long)
}

@Dao
interface PlannedBallAlcoholDao : BaseDao<PlannedBallAlcoholEntity> {
    @Query("SELECT * FROM planned_ball_alcohol WHERE plannedBallId = :plannedBallId")
    suspend fun forBall(plannedBallId: Long): List<PlannedBallAlcoholEntity>

    @Query("DELETE FROM planned_ball_alcohol WHERE plannedBallId = :ballId")
    suspend fun deleteForBall(ballId: Long)
}