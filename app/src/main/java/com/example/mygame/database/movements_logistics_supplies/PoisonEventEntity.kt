package com.example.mygame.database.movements_logistics_supplies

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "poison_event")
data class PoisonEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val turn: Int
)