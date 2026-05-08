package com.example.mygame.database.politics_diplomacy_succession

enum class FactionId(val index: Int) {
    FACTION_0(0), FACTION_1(1), FACTION_2(2), FACTION_3(3),
    FACTION_4(4), FACTION_5(5), FACTION_6(6), REBELS(7);
    companion object { fun of(i: Int) = entries.first { it.index == i } }
}