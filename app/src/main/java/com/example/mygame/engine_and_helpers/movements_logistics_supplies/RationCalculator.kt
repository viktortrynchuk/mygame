package com.example.mygame.engine_and_helpers.movements_logistics_supplies

/** Helper to compute minimal food/water turns carried by a party. */
class RationCalculator {
    data class Need(val foodTurns: Int, val waterTurns: Int)

    fun estimateFor(people: Int, animals: Int, barrelsWater: Int, loaves: Int, cheese: Int): Need {
        val waterPerTurn = (people + animals)
        val waterTurns = if (waterPerTurn == 0) 0 else (barrelsWater * 50) / waterPerTurn
        val foodSatietyPerTurn = people // assume 1 satiety per person per 2 turns averaged
        val totalFoodSatiety = loaves * 4 + cheese * 3
        val foodTurns = if (foodSatietyPerTurn == 0) 0 else totalFoodSatiety / foodSatietyPerTurn
        return Need(foodTurns = foodTurns, waterTurns = waterTurns)
    }
}