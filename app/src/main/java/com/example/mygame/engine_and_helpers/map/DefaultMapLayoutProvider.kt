package com.example.mygame.engine_and_helpers.map

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultMapLayoutProvider @Inject constructor() : MapLayoutProvider {

    override fun landAnchor(
        landId: Long,
        viewW: Int,
        viewH: Int
    ): Pair<Int, Int> {

        // TEMP simple layout: spread lands in grid
        val col = (landId % 4).toInt()
        val row = (landId / 4).toInt()

        val x = viewW * (col + 1) / 5
        val y = viewH * (row + 1) / 5

        return x to y
    }

    override fun borderMidpoint(
        fromLandId: Long,
        toLandId: Long,
        viewW: Int,
        viewH: Int
    ): Pair<Int, Int> {

        val (x1, y1) = landAnchor(fromLandId, viewW, viewH)
        val (x2, y2) = landAnchor(toLandId, viewW, viewH)

        return ((x1 + x2) / 2) to ((y1 + y2) / 2)
    }
}