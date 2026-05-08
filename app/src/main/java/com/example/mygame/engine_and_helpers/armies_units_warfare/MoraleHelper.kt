package com.example.mygame.engine_and_helpers.armies_units_warfare

/** Helper to estimate morale modifier for a unit based on recent events. */
class MoraleHelper {
    fun modifier(wonLast: Boolean, foodOk: Boolean, commanderRespected: Boolean, casualtiesPct: Int): Int {
        var mod = 0
        if (wonLast) mod += 10 else mod -= 10
        if (!foodOk) mod -= 10
        if (commanderRespected) mod += 5
        mod -= (casualtiesPct / 10)
        return mod
    }
}