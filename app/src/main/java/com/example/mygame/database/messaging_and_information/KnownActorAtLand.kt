package com.example.mygame.database.messaging_and_information

data class KnownActorAtLand(
    val actorId: Long,
    val name: String?,          // optional until you have a real actor table wired
    val imprisoned: Boolean?,   // null if unknown
    val lastSeenTurn: Int
)
