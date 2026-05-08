package com.example.mygame.database.politics_diplomacy_succession

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "casus_belli")
data class CasusBelliEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val countryA: Long,
    val countryB: Long,
    val reason: String,
    val turn: Int
)