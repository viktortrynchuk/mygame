package com.example.mygame.engine_and_helpers.ui_facades

import com.example.mygame.engine_and_helpers.justice_and_court.JusticeService
import javax.inject.Inject
import javax.inject.Singleton

interface JusticeFacade {
    suspend fun docketFor(landId: Long): CourtDocket
}

@Singleton
class JusticeFacadeImpl @Inject constructor(
    private val justice: JusticeService
) : JusticeFacade {
    override suspend fun docketFor(landId: Long): CourtDocket {
        val crimes = justice.crimesIn(landId)
        val trials = crimes.mapNotNull { justice.trialFor(it.id) }
        val verdicts = trials.mapNotNull { justice.verdictFor(it.id) }
        return CourtDocket(crimes, trials, verdicts)
    }
}