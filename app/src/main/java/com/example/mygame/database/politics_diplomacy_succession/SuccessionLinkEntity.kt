package com.example.mygame.database.politics_diplomacy_succession

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "succession_link",
    indices = [Index(value = ["rulerId"], name = "idx_succession_ruler")]
)
data class SuccessionLinkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rulerId: Long,
    val heirId: Long
)