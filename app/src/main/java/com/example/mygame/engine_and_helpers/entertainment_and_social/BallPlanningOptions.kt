package com.example.mygame.engine_and_helpers.entertainment_and_social

data class BallPlanningOptions(
    val possibleTurns: List<Int>,
    val bards: List<BardCandidate>,
    val attendees: List<AttendeeCandidate>,
    val venues: List<VenueOption>,
    val alcoholStocks: List<AlcoholStockOption>,
    val marketAlcohol: List<MarketAlcoholOption>,
    val budgetStocks: List<AlcoholStockOption>
)