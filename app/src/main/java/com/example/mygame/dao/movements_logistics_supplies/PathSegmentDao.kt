package com.example.mygame.dao.movements_logistics_supplies

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.movements_logistics_supplies.PathSegmentEntity

@Dao
interface PathSegmentDao : BaseDao<PathSegmentEntity> {
    @Query("SELECT * FROM path_segment WHERE movementOrderId = :orderId")
    suspend fun forOrder(orderId: Long): List<PathSegmentEntity>
}