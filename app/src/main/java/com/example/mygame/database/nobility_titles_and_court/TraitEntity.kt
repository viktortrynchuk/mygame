package com.example.mygame.database.nobility_titles_and_court

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "trait",
    indices = [Index(value = ["nobleId"], name = "idx_trait_noble")]
)
data class TraitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nobleId: Long,
    val key: String,
    val value: Int
)