package com.example.mygame.dao.entertainment_and_social

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.entertainment_and_social.BallEventEntity

@Dao
interface BallDao : BaseDao<BallEventEntity> {
    @Query("SELECT * FROM ball_event WHERE landId = :landId and turn = :turn")
    suspend fun ballInLand(landId: Long, turn: Int): List<BallEventEntity>

    @Query("SELECT * FROM ball_event WHERE turn = :turn")
    suspend fun forTurn(turn: Int): List<BallEventEntity>
}