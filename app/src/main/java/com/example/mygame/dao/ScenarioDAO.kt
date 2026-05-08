package com.example.mygame.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mygame.database.persistence_and_game_state.ScenarioEntity

@Dao
interface ScenarioDAO {
    //Returns one row (value 1) if NOT empty, returns zero rows if empty
    @Query("SELECT 1 FROM scenario LIMIT 1")
    suspend fun checkIfEmpty(): Long

    @Insert
    suspend fun insertScenario(scenario: ScenarioEntity): Long

    @Query("SELECT * FROM scenario")
    suspend fun getAllScenarios(): List<ScenarioEntity>

    @Query("DELETE FROM scenario")
    suspend fun deleteAll(): Int   // rows deleted (use Unit if you don't care)
}