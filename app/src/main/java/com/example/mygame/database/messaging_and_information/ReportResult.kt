package com.example.mygame.database.messaging_and_information

data class ReportResult(
    val landId: Long,
    val population: Int?,
    val satisfaction: Int?,
    val resources: Map<String, Int>,
    val foodTurns: Int?,
    val state: String?, // "UNDER_SIEGE", "FAMINE", "PROSPERITY" etc.
    val events: List<Pair<String, Int>> // (eventName, plannedTurn)
)