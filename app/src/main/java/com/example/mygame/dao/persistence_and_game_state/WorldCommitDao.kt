package com.example.mygame.dao.persistence_and_game_state

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.persistence_and_game_state.WorldCommitEntity

@Dao
interface WorldCommitDao : BaseDao<WorldCommitEntity> {
    @Query("INSERT INTO world_commit(turn, checksum, createdAt) VALUES(:turn,:checksum,:createdAt)")
    suspend fun commit(turn: Int, checksum: String, createdAt: Long)

    @Query("SELECT * FROM world_commit ORDER BY id DESC LIMIT 1")
    suspend fun lastCommit(): WorldCommitEntity?
}