package com.example.mygame.database.politics_diplomacy_succession

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ambassador",
    indices = [Index(value = ["countryId"], name = "idx_ambassador_country")]
)
data class AmbassadorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val countryId: Long,
    val landId: Long,
    val imprisoned: Boolean
)