package com.example.mygame.dao.armies_units_warfare

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.armies_units_warfare.BattleParticipantEntity

@Dao
interface BattleParticipantDao : BaseDao<BattleParticipantEntity> {
    @Query("SELECT * FROM battle_participant WHERE battleId = :battleId")
    suspend fun forBattle(battleId: Long): List<BattleParticipantEntity>
}