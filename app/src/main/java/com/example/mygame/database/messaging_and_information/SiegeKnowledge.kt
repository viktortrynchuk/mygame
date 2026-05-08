package com.example.mygame.database.messaging_and_information

data class SiegeKnowledge(
    val attackerFactionIndex: Int,  // 0..7
    val defenderLandId: Long,
    val turn: Int
)