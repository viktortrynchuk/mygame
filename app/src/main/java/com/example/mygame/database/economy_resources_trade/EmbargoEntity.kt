package com.example.mygame.database.economy_resources_trade

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "embargo")
data class EmbargoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val countryA: Long,
    val countryB: Long,
    val active: Boolean
)