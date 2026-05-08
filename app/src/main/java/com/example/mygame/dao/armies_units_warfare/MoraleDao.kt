package com.example.mygame.dao.armies_units_warfare

import com.example.mygame.database.armies_units_warfare.MoraleEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mygame.dao.BaseDao

@Dao
interface MoraleDao : BaseDao<MoraleEntity> {

    @Query("DELETE FROM morale")
    suspend fun deleteAll()
}