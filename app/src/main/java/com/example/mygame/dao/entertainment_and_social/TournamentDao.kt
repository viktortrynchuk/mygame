package com.example.mygame.dao.entertainment_and_social

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.entertainment_and_social.TournamentEntity

@Dao
interface TournamentDao : BaseDao<TournamentEntity> {
    @Query("SELECT * FROM tournament WHERE landId = :landId")
    suspend fun inLand(landId: Long): List<TournamentEntity>
}