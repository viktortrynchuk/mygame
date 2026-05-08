package com.example.mygame.database.movements_logistics_supplies

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "wagon",
    indices = [Index(value = ["convoyId"], name = "idx_wagon_convoy")]
)
data class WagonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val convoyId: Long,
    val capacity: Int
)