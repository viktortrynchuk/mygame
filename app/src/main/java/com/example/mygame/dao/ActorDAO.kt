package com.example.mygame.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mygame.database.Actor
import com.example.mygame.database.ActorLocation

@Dao
interface ActorDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(actor: Actor)

    @Query("SELECT * FROM actor WHERE scenarioId = :scenarioId")
    suspend fun actorsForScenario(scenarioId: Long): List<Actor>

    @Query("SELECT * FROM actor WHERE scenarioId = :scenarioId AND actorType = :actorType")
    suspend fun actorsOfTypeForScenario(scenarioId: Long, actorType: String): List<Actor>

    @Query("SELECT * FROM actor WHERE scenarioId = :scenarioId AND actorId = :actorId")
    suspend fun byId(scenarioId: Long, actorId: Long): Actor?
}

@Dao
interface ActorLocationDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(location: ActorLocation)

    @Query("SELECT * FROM actor_location WHERE scenarioId = :scenarioId AND actorId = :actorId")
    suspend fun getLocation(scenarioId: Long, actorId: Long): ActorLocation?
}