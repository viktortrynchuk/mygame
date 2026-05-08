package com.example.mygame.engine_and_helpers.entertainment_and_social

import javax.inject.Inject
import javax.inject.Singleton

/** Calculates mood/prestige deltas from social events. */
@Singleton
class EntertainmentImpactCalculator @Inject constructor() {
    data class Impact(val moodDelta: Int, val prestigeDelta: Int)
    fun forBall(wineBarrels: Int, bardRank: Int, scandal: Boolean): Impact {
        var mood = 5 + wineBarrels
        var prestige = 2 + bardRank
        if (scandal) { mood -= 4; prestige += 3 }
        return Impact(mood.coerceIn(-10, 20), prestige.coerceIn(0, 30))
    }
    fun forFestival(visitingLeader: Boolean, beerBarrels: Int): Impact {
        val mood = 8 + beerBarrels
        val prestige = if (visitingLeader) 10 else 3
        return Impact(mood.coerceIn(0, 25), prestige)
    }
    fun forHunt(bestKill: Int, injuries: Int): Impact {
        val mood = 3 + bestKill - injuries
        val prestige = (bestKill / 2) + if (injuries > 0) -2 else 2
        return Impact(mood.coerceIn(-5, 15), prestige.coerceIn(-5, 15))
    }
}