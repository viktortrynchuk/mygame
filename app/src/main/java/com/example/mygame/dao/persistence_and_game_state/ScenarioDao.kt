package com.example.mygame.dao.persistence_and_game_state

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.persistence_and_game_state.ScenarioEntity

@Dao
//interface ScenarioDao {
interface ScenarioDao : BaseDao<ScenarioEntity> {
    //Returns one row (value 1) if NOT empty, returns zero rows if empty
//    @Query("SELECT 1 FROM scenario LIMIT 1")
    @Query("SELECT EXISTS(SELECT 1 FROM scenario)")
    suspend fun checkIfEmpty(): Long

    @Insert
    suspend fun insertScenario(scenarioEntity: ScenarioEntity): Long

    @Query("SELECT * FROM scenario")
    suspend fun getAllScenarios(): List<ScenarioEntity>

    @Query("DELETE FROM scenario")
    suspend fun deleteAll(): Int   // rows deleted (use Unit if you don't care)

    @Query("SELECT * FROM scenario ORDER BY scenarioId")
    suspend fun list(): List<ScenarioEntity>

    @Query("SELECT * FROM scenario WHERE scenarioId = :id")
    suspend fun get(id: Long): ScenarioEntity?

    @Query("UPDATE scenario SET isActive = false")
    suspend fun clearActive()

    @Query("UPDATE scenario SET isActive = true WHERE scenarioId = :id")
    suspend fun markActive(id: Long)

    @Query("SELECT * FROM scenario WHERE isActive = true LIMIT 1")
    suspend fun active(): ScenarioEntity?

    @Transaction
    suspend fun activate(id: Long) { clearActive(); markActive(id) }
}
