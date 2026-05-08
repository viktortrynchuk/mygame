package com.example.mygame.database.world_and_geography

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "river")
data class RiverEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val sourceType: String,
    val sinkType: String
)