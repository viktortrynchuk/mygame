package com.example.mygame.engine_and_helpers.justice_and_court

/** Rough justice policy helper for sentencing severity. */
class SentencingGuide {
    enum class Crime { MURDER, ATTEMPTED_MURDER, DUEL, ROBBERY, BANDITRY, REVOLT, WITCHCRAFT, FORGERY, ESPIONAGE, COUNTERFEIT, BLACK_MARKET }
    enum class Pun { EXECUTION, IMPRISONMENT, FINE, EXILE, FORCED_MONK }

    fun recommend(crime: Crime, nobilityRankHigh: Boolean, bribed: Boolean, superstition: Int): Pun = when (crime) {
        Crime.MURDER -> if (bribed) Pun.IMPRISONMENT else Pun.EXECUTION
        Crime.ATTEMPTED_MURDER -> if (nobilityRankHigh) Pun.IMPRISONMENT else Pun.EXECUTION
        Crime.DUEL -> if (nobilityRankHigh) Pun.FINE else Pun.IMPRISONMENT
        Crime.ROBBERY, Crime.BANDITRY -> Pun.EXECUTION
        Crime.REVOLT -> Pun.EXECUTION
        Crime.WITCHCRAFT -> if (superstition > 60) Pun.EXECUTION else Pun.EXILE
        Crime.FORGERY, Crime.ESPIONAGE, Crime.COUNTERFEIT -> Pun.IMPRISONMENT
        Crime.BLACK_MARKET -> Pun.FINE
    }
}