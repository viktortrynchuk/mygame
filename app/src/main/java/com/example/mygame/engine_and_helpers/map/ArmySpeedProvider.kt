package com.example.mygame.engine_and_helpers.map

/** Determines how far an army moves per turn (tiles/edges per turn). */
interface ArmySpeedProvider {
    /** Speed in edges per turn; use integers or fractions (0.5f) if you like. */
    fun speedPerTurn(armyId: Long): Float
}