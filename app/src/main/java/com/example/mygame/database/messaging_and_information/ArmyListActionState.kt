package com.example.mygame.database.messaging_and_information

data class ArmyListActionState(
    val armies: List<ArmyInfo>,
    val canRefreshLocally: Boolean,
    val refreshRole: String? = null
)