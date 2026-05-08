package com.example.mygame.dao.politics_diplomacy_succession

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.politics_diplomacy_succession.SuccessionLinkEntity

@Dao
interface SuccessionDao : BaseDao<SuccessionLinkEntity> {
    @Query("SELECT * FROM succession_link WHERE rulerId = :rulerId")
    suspend fun heirs(rulerId: Long): List<SuccessionLinkEntity>
}