package com.example.mygame.dao.dignity_duels_conflicts

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.dignity_duels_conflicts.PersonalConflictEntity

@Dao
interface ConflictDao : BaseDao<PersonalConflictEntity> {
    @Query("SELECT * FROM personal_conflict WHERE nobleA = :nobleId OR nobleB = :nobleId")
    suspend fun involving(nobleId: Long): List<PersonalConflictEntity>
}