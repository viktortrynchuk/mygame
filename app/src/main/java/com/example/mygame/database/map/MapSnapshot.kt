package com.example.mygame.database.map

data class MapSnapshot(
    val settlements: List<SettlementMarker>,
    val flags: List<FlagMarker>
)
