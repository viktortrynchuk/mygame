package com.example.mygame.engine_and_helpers.map

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultArmySpeedProvider @Inject constructor() : ArmySpeedProvider {

    override fun speedPerTurn(armyId: Long): Float {
        // Basic temporary logic:
        // You can later read from ArmyEntity, terrain, supplies, etc.

        return 1.0f   // 1 edge per turn
    }
}
