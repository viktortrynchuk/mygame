package com.example.mygame.dao.dignity_duels_conflicts

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.dignity_duels_conflicts.DuelEntity

@Dao
interface DuelDao : BaseDao<DuelEntity> {
    @Query("SELECT * FROM duel WHERE participantA = :nobleId OR participantB = :nobleId")
    suspend fun forNoble(nobleId: Long): List<DuelEntity>
}