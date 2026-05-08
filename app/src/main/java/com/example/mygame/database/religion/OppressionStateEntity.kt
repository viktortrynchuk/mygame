package com.example.mygame.database.religion

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "oppression_state")
data class OppressionStateEntity(
    @PrimaryKey val landId: Long,
    val oppressedReligionId: Long?,
    val oppressedNationalityId: Long?
)