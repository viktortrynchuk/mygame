package com.example.mygame.database.rebellion_banditry

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "outlaw")
data class OutlawEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val banditGroupId: Long,
    val name: String
)