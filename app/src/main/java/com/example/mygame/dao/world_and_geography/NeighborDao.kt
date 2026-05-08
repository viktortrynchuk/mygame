package com.example.mygame.dao.world_and_geography

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.world_and_geography.LandEntity
import com.example.mygame.database.world_and_geography.NeighborEntity

@Dao
interface NeighborDao : BaseDao<NeighborEntity> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: NeighborEntity)

    @Query("DELETE FROM neighbor")
    suspend fun deleteAll()
}