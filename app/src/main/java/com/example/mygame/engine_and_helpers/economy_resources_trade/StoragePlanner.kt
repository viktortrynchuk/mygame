package com.example.mygame.engine_and_helpers.economy_resources_trade

/** Simple helper to compute storage volume and loss risk outside warehouses. */
class StoragePlanner {
    fun volumeOf(itemId: String, qty: Int): Int = when (itemId) {
        "WOOD" -> qty * 1
        "SHIP_WOOD" -> qty * 10
        "STONE", "CLAY" -> qty * 10
        "GOLD" -> qty * 500
        "SILVER" -> qty * 200
        "COPPER", "IRON", "LEAD" -> qty * 200
        "COAL", "CROP", "GRAPE", "HEMP", "SALT", "WATER", "MILK", "BEER", "WINE" -> qty * 1
        else -> qty // default 1:1 for crafted goods bucket
    }

    /** Percent loss per 60 turns when over capacity */
    fun lossPercentOutsideWarehouse(itemId: String): Int = when (itemId) {
        "COAL", "CROP", "SALT", "WATER", "MILK" -> 15
        else -> 5
    }
}