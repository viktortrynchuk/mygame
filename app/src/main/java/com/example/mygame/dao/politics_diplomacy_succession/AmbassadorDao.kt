package com.example.mygame.dao.politics_diplomacy_succession

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.politics_diplomacy_succession.AmbassadorEntity

@Dao
interface AmbassadorDao : BaseDao<AmbassadorEntity> {
    @Query("SELECT * FROM ambassador WHERE countryId = :countryId")
    suspend fun byCountry(countryId: Long): List<AmbassadorEntity>
}