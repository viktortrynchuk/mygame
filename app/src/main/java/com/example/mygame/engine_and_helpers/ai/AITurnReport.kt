package com.example.mygame.engine_and_helpers.ai

data class AITurnReport(
    val messagesSent: Int,
    val ordersIssued: Int,
    val battlesStarted: Int,
    val rebelsSuppressed: Int
)