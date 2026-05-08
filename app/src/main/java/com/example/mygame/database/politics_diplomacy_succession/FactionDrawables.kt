package com.example.mygame.database.politics_diplomacy_succession

import com.example.mygame.R

object FactionDrawables {
    fun flagFor(faction: FactionId): Int = when (faction) {
        FactionId.FACTION_0 -> R.drawable.flag0
        FactionId.FACTION_1 -> R.drawable.flag1
        FactionId.FACTION_2 -> R.drawable.flag2
        FactionId.FACTION_3 -> R.drawable.flag3
        FactionId.FACTION_4 -> R.drawable.flag4
        FactionId.FACTION_5 -> R.drawable.flag5
        FactionId.FACTION_6 -> R.drawable.flag6
        FactionId.REBELS    -> R.drawable.flag7_rebels
    }
}