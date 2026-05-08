package com.example.mygame.database.map

data class MapMenuItem(
    val action: MapMenuAction,
    val enabled: Boolean,
    val title: String
)