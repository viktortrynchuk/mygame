package com.example.mygame.database.population_and_society

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nationality")
data class NationalityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)