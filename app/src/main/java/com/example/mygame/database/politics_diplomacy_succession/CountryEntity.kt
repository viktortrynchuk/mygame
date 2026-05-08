package com.example.mygame.database.politics_diplomacy_succession

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "country")
data class CountryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)