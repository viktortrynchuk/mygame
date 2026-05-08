package com.example.mygame.dao.foundations_core

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.foundations_core.TurnClockEntity

@Dao
interface TurnClockDao : BaseDao<TurnClockEntity> {
    @Query("SELECT * FROM turn_clock WHERE id = 1")
    suspend fun getSingleton(): TurnClockEntity?

    @Transaction
    suspend fun incrementTurn(): TurnClockEntity {
        val curr = getSingleton() ?: TurnClockEntity(turn = 0, isNight = false, season = "SPRING", seed = 0)
        val next = curr.copy(turn = curr.turn + 1, isNight = !curr.isNight)
        upsert(next)
        return next
    }
}
