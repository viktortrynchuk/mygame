package com.example.mygame.engine_and_helpers.rebellion_banditry

/** Simple helper to estimate if a rebellion should spark this turn. */
class UprisingEvaluatorHelper {
    /**
     * @param loyalty (0..100), @param satisfaction (0..100), @param oppressionBonus extra weight if religion/nationality oppressed
     */
    fun chancePercent(loyalty: Int, satisfaction: Int, oppressionBonus: Int, famine: Boolean): Int {
        var p = 0
        p += (100 - loyalty) / 3
        p += (100 - satisfaction) / 2
        p += oppressionBonus
        if (famine) p += 10
        return p.coerceIn(0, 100)
    }
}