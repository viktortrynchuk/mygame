package com.example.mygame.database.messaging_and_information

import com.example.mygame.database.map.ActorView

data class ActorListActionState(
    val actors: List<ActorView>,
    val canRefreshLocally: Boolean,
    val refreshRole: String
)