package com.example.mygame.database.movements_logistics_supplies

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "route")
data class RouteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)