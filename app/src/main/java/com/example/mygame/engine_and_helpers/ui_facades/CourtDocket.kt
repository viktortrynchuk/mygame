package com.example.mygame.engine_and_helpers.ui_facades

import com.example.mygame.database.justice_and_court.CrimeEntity
import com.example.mygame.database.justice_and_court.TrialEntity
import com.example.mygame.database.justice_and_court.VerdictEntity

data class CourtDocket(
    val crimes: List<CrimeEntity>,
    val trials: List<TrialEntity>,
    val verdicts: List<VerdictEntity>
)