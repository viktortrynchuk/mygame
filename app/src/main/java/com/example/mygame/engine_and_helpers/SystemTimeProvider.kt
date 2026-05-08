package com.example.mygame.engine_and_helpers

import javax.inject.Inject

/** Abstraction over time/random for determinism in tests */
interface TimeProvider {
    fun nowMillis(): Long
}

class SystemTimeProvider @Inject constructor() : TimeProvider {
    override fun nowMillis(): Long = System.currentTimeMillis()
}