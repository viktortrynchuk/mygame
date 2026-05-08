package com.example.mygame.engine_and_helpers.map

/** Converts landIds (and land-to-land edges) to screen coordinates in SecondView. */
interface MapLayoutProvider {
    /** Anchor point for settlement/land flag. Must be within [0, viewW] x [0, viewH]. */
    fun landAnchor(landId: Long, viewW: Int, viewH: Int): Pair<Int, Int>

    /** Midpoint for the border between two lands (for “moving” flags). */
    fun borderMidpoint(fromLandId: Long, toLandId: Long, viewW: Int, viewH: Int): Pair<Int, Int>
}