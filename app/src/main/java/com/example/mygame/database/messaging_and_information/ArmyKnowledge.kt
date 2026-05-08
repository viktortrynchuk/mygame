package com.example.mygame.database.messaging_and_information

data class ArmyKnowledge(
    val armyId: Long,
    val landId: Long,
    val factionIndex: Int,        // 0..7
    val turn: Int                 // when the knowledge was obtained
)
