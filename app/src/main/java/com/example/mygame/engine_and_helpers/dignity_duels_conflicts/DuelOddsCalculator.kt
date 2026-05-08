package com.example.mygame.engine_and_helpers.dignity_duels_conflicts

import javax.inject.Inject
import javax.inject.Singleton

/** Computes odds of winning a duel given traits. */
@Singleton
class DuelOddsCalculator @Inject constructor() {
    data class Traits(
        val strength: Int, // 0..100
        val caution: Int,
        val superstition: Int,
        val beauty: Int // affects morale/resolve
    )
    fun winChance(self: Traits, opponent: Traits): Int {
        var score = 0.0
        score += (self.strength - opponent.strength) * 0.7
        score += (opponent.caution - self.caution) * 0.2 // reckless opponent helps
        score += (self.beauty - opponent.beauty) * 0.1 // panache!
        score -= (self.superstition - opponent.superstition) * 0.05 // jitters
        val p = 50 + score
        return p.toInt().coerceIn(5, 95)
    }
}