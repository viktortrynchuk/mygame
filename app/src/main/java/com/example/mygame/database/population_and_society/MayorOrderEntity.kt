package com.example.mygame.database.population_and_society

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mayor_order",
    indices = [Index(value = ["landId"], name = "idx_mayor_order_land")]
)
data class MayorOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val landId: Long,
    val type: String,
    val payload: String
)