package com.example.mygame.dao.armies_units_warfare

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.armies_units_warfare.ArmyEntity

@Dao
interface ArmyDao : BaseDao<ArmyEntity> {

    @Query("SELECT * FROM army WHERE id = :id")
    suspend fun get(id: Long): ArmyEntity?

    @Query("SELECT * FROM army ORDER BY id")
    suspend fun all(): List<ArmyEntity>

    @Query("SELECT * FROM army WHERE landId = :landId ORDER BY id")
    suspend fun inLand(landId: Long): List<ArmyEntity>

    @Query("SELECT * FROM army WHERE countryId = :countryId ORDER BY id")
    suspend fun forCountry(countryId: Long): List<ArmyEntity>

    @Query("UPDATE army SET landId = :toLandId WHERE id = :armyId")
    suspend fun moveToLand(armyId: Long, toLandId: Long)

    @Query("UPDATE army SET morale = :morale WHERE id = :armyId")
    suspend fun setMorale(armyId: Long, morale: Int)

    @Query("UPDATE army SET commanderActorId = :commanderId WHERE id = :armyId")
    suspend fun assignCommander(armyId: Long, commanderId: Long?)
}
