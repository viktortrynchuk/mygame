package com.example.mygame.engine_and_helpers.messaging_and_information

// --- Compact facts codec (one-line per fact) ---
sealed class Fact {
    data class LandOwner(val landId: Long, val ownerType: String, val ownerRef: Long) : Fact()
    data class LandSatisfaction(val landId: Long, val level: Int) : Fact()
    data class StructureKnown(val landId: Long, val type: String) : Fact()
    data class FortLevel(val landId: Long, val level: Int) : Fact()
    data class FloodTurns(val landId: Long, val turns: Int) : Fact()
    data class PoisonedUntil(val landId: Long, val turn: Int) : Fact()
    data class MarketKnown(val landId: Long) : Fact()
    data class TemplesCount(val landId: Long, val count: Int) : Fact()
    data class MonasteriesCount(val landId: Long, val count: Int) : Fact()
    data class ArmyAtLand(val armyId: Long, val countryId: Long, val landId: Long) : Fact()
    data class OrderEcho(val armyId: Long, val order: String, val turn: Int) : Fact()
    data class DiplomacyStatus(val a: Long, val b: Long, val status: String) : Fact()
    data class RebellionSighted(val landId: Long) : Fact()
    data class CropShortage(val landId: Long) : Fact()
    data class CrimeReported(val landId: Long, val crimeId: Long) : Fact()
    data class TrialOpened(val crimeId: Long, val trialId: Long) : Fact()
    data class VerdictKnown(val trialId: Long, val guilty: Boolean) : Fact()
}