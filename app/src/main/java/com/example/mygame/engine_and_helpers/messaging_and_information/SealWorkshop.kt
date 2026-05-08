package com.example.mygame.engine_and_helpers.messaging_and_information

/** Helper to estimate a spy's chance to forge a seal believably. */
class SealWorkshop {
    fun estimateAccuracy(spySkill: Int, waxQuality: Int, stampMatch: Int): Int {
        // Very simple curve — callers can tune inputs (0..100)
        val base = (spySkill * 0.6 + waxQuality * 0.2 + stampMatch * 0.2)
        return base.toInt().coerceIn(0, 100)
    }
}