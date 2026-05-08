package com.example.mygame.database.armies_units_warfare

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bullet_craft_order")
data class BulletCraftOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val armyId: Long,
    val batches: Int
)