package com.example.mygame.database.map

import com.example.mygame.database.politics_diplomacy_succession.FactionId

data class FlagMarker(
    val key: String,                 // stable unique key for diffing
    val landId: Long,                // destination if MOVING, otherwise current land
    val faction: FactionId,
    val kind: FlagKind,
    val screenX: Int,
    val screenY: Int,
    val aggregatedArmyCount: Int,
    val label: String? = null        // optional overlay (e.g., “ETA 2t”)
)