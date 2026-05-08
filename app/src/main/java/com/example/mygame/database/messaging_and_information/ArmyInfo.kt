package com.example.mygame.database.messaging_and_information

import com.example.mygame.database.politics_diplomacy_succession.FactionId

data class ArmyInfo(
    val armyId: Long,
    val name: String?,
    val faction: FactionId,
    val lastKnownLandId: Long,
    val movingEta: Int? // null if stationary
)