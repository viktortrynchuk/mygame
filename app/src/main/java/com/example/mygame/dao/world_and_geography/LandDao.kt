package com.example.mygame.dao.world_and_geography

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.world_and_geography.LandEntity
import com.example.mygame.database.world_and_geography.NeighborEntity

@Dao
interface LandDao : BaseDao<LandEntity> {
    @Query("SELECT * FROM land WHERE id = :id")
    suspend fun get(id: Long): LandEntity?

    @Query("SELECT * FROM land WHERE countryId = :countryId")
    suspend fun byCountry(countryId: Long): List<LandEntity>

    @Query("SELECT n.neighborId FROM neighbor n WHERE n.landId = :id")
    suspend fun neighborIds(id: Long): List<Long>

    @Query("SELECT COUNT(*) FROM land")
    suspend fun countAll(): Int

    @Query("SELECT * FROM land")
    suspend fun getAll(): List<LandEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNeighbor(entity: NeighborEntity)

}