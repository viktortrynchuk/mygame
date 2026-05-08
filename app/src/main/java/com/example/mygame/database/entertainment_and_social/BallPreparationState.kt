package com.example.mygame.database.entertainment_and_social

data class BallPreparationState(
    val ballId: Long,
    val landId: Long,
    val eventTurn: Int,
    val venueStructureId: Long?,
    val hasVenue: Boolean,
    val venueOwnershipOk: Boolean,
    val hasBards: Boolean,
    val hasAlcohol: Boolean,
    val guestsPrepared: Boolean,
    val deliveryCapacityOk: Boolean,
    val organizerPresent: Boolean,
    val missingItems: List<String>,
    val ready: Boolean,

    val acceptedBardCount: Int = 0,
    val acceptedGuestCount: Int = 0,
    val plannedBardCount: Int = 0,
    val plannedGuestCount: Int = 0,
    val readyToSendDelegationOrder: Boolean = false,
    val readyToRelease: Boolean = false,
    val budgetAvailable: Boolean = false,
    val delegated: Boolean = false
)