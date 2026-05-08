package com.example.mygame.dao.armies_units_warfare

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.armies_units_warfare.UnitCompositionEntity

@Dao
interface UnitCompositionDao : BaseDao<UnitCompositionEntity> {

    @Query("DELETE FROM unit_composition")
    suspend fun deleteAll()
}