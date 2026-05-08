package com.example.mygame.dao.world_and_geography

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.world_and_geography.OwnershipEntity

@Dao
interface OwnershipDao : BaseDao<OwnershipEntity> {
    @Query("SELECT * FROM ownership WHERE landId = :landId")
    suspend fun ownerOf(landId: Long): OwnershipEntity?

    @Query("UPDATE ownership SET ownerType = :ownerType, ownerRef = :ownerRef WHERE landId = :landId")
    suspend fun setOwner(landId: Long, ownerType: String, ownerRef: Long)

    @Query("SELECT COUNT(1) FROM ownership WHERE landId = :landId AND (ownerType = 'PLAYER' OR ownerType = 'VASSAL')")
    suspend fun countOwnedByPlayerOrVassal(landId: Long): Int
}

suspend fun OwnershipDao.isOwnedByPlayerOrVassal(landId: Long) =
    countOwnedByPlayerOrVassal(landId) > 0