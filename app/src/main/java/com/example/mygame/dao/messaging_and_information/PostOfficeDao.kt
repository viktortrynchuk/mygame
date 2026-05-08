package com.example.mygame.dao.messaging_and_information

import androidx.room.Dao
import androidx.room.Query
import com.example.mygame.dao.BaseDao
import com.example.mygame.database.messaging_and_information.PostOfficeEntity

@Dao
interface PostOfficeDao : BaseDao<PostOfficeEntity> {
    @Query("SELECT * FROM post_office WHERE landId = :landId")
    suspend fun get(landId: Long): PostOfficeEntity?
}