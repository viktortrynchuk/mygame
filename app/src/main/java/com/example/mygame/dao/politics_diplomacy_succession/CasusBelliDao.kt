package com.example.mygame.dao.politics_diplomacy_succession

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.politics_diplomacy_succession.CasusBelliEntity

@Dao
interface CasusBelliDao : BaseDao<CasusBelliEntity> {
    @Query("SELECT * FROM casus_belli WHERE (countryA = :a AND countryB = :b) OR (countryA = :b AND countryB = :a)")
    suspend fun between(a: Long, b: Long): List<CasusBelliEntity>
}