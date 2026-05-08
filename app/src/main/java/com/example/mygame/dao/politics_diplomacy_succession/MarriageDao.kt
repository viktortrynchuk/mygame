package com.example.mygame.dao.politics_diplomacy_succession

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.politics_diplomacy_succession.MarriageProposalEntity

@Dao
interface MarriageDao : BaseDao<MarriageProposalEntity> {
    @Query("SELECT * FROM marriage_proposal WHERE toNobleId = :nobleId AND status = 'PENDING'")
    suspend fun pendingFor(nobleId: Long): List<MarriageProposalEntity>
}