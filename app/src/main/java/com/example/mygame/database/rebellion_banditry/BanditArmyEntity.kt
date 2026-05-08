package com.example.mygame.database.rebellion_banditry

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bandit_army")
data class BanditArmyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val strength: Int
)