package com.example.mygame.database.entertainment_and_social

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "performance")
data class PerformanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val festivalId: Long,
    val troupe: String,
    val rating: Int?
)