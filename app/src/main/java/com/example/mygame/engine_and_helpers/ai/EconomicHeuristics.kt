package com.example.mygame.engine_and_helpers.ai

import com.example.mygame.engine_and_helpers.economy_resources_trade.InventoryService

/** Tiny heuristics for economy signals used by AI. */
class EconomicHeuristics {
    suspend fun isShortOnCrops(landId: Long, inv: InventoryService): Boolean {
        val stocks = inv.stockIn(landId)
        val crop = stocks.firstOrNull { it.itemId == "CROP" }?.qty ?: 0
        return crop < 50
    }
}