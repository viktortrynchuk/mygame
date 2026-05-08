package com.example.mygame.database.movements_logistics_supplies

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "secret_route")
data class SecretRouteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fromLandId: Long,
    val toLandId: Long,
    val risk: Int
)