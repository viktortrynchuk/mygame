package com.example.mygame.dao.rebellion_banditry

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.armies_units_warfare.ArmyEntity
import com.example.mygame.database.rebellion_banditry.RebelArmyEntity

@Dao
interface RebelArmyDao : BaseDao<RebelArmyEntity> {

    @Query("SELECT * FROM rebel_army WHERE rebellionId = :rebellionId AND (disbandedTurn IS NULL)")
    suspend fun forRebellion(rebellionId: Long): List<RebelArmyEntity>

    @Query("""
        SELECT a.* FROM army a
        JOIN rebel_army r ON r.armyId = a.id
        WHERE r.rebellionId = :rebellionId AND (r.disbandedTurn IS NULL)
    """)
    suspend fun armiesForRebellion(rebellionId: Long): List<ArmyEntity>

    @Query("DELETE FROM rebel_army WHERE armyId = :armyId")
    suspend fun deleteByArmy(armyId: Long)
}