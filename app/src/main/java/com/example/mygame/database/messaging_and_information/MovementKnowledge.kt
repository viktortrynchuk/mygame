package com.example.mygame.database.messaging_and_information

data class MovementKnowledge(
    val armyId: Long,
    val fromLandId: Long,
    val toLandId: Long,
    val createdTurn: Int,
    val path: List<Long>            // path lands including toLandId
)
