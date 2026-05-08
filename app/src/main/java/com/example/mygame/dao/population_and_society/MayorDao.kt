package com.example.mygame.dao.population_and_society

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.population_and_society.MayorEntity

@Dao
interface MayorDao : BaseDao<MayorEntity> {
    @Query("SELECT * FROM mayor WHERE landId = :landId")
    suspend fun get(landId: Long): MayorEntity?

    @Query("SELECT * FROM mayor WHERE actorId = :actorId")
    suspend fun byActorId(actorId: Long): MayorEntity?
}