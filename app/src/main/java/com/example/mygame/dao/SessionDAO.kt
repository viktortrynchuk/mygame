package com.example.mygame.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.mygame.database.persistence_and_game_state.CurrentSession

@Dao
interface SessionDAO {
    // Update an existing session
    @Update
    suspend fun update(session: CurrentSession)

    // Delete a session from the database
    @Delete
    suspend fun delete(session: CurrentSession)

    @Insert
    suspend fun insertSession(session: CurrentSession):Long

    @Query("SELECT * FROM session")
    suspend fun getAllSessions(): List<CurrentSession>

    @Query("DELETE FROM session")
    suspend fun deleteAll(): Int   // rows deleted (use Unit if you don't care)
}