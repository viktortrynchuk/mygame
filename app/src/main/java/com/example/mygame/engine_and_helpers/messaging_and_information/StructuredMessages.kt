package com.example.mygame.engine_and_helpers.messaging_and_information

import com.example.mygame.database.messaging_and_information.MessageType

enum class OrderKind {
    COLLECT_TAX_FROM_LANDS,
    COLLECT_TAX_FROM_ACTORS,
    IMPRISON_ACTORS,
    RELEASE_ACTORS,
    COLLECT_ARMY_IN_LANDS,
    DISBAND_ARMIES,
    SELL_GOODS,
    BUY_GOODS,
    CHECK_LOYALTY_LANDS,
    CHECK_LOYALTY_ACTORS,
    ORGANIZE_REBELLION,
    OFFER_ALLIANCE,
    OFFER_PEACE,
    DECLARE_WAR,
    INTERCEPT_CORRESPONDENCE,
    ORGANIZE_BALL,
    ORGANIZE_FESTIVAL,
    ORGANIZE_RELIGIOUS_FESTIVAL,
    ORGANIZE_HUNT,
    INVITE_TO_BALL,
    INVITE_TO_FESTIVAL,
    INVITE_TO_RELIGIOUS_FESTIVAL,
    INVITE_TO_HUNT,
    TRANSPORT_RESOURCES,
    TRANSPORT_MONEY,
    BUY_ALCOHOL_FOR_EVENT,
    HIRE_BARDS_FOR_EVENT,
    CALL_FOR_DUEL,
    ORDER_KILL_ACTORS,
    COLLECT_INFO_ABOUT_ACTORS,
    COLLECT_INFO_ABOUT_LANDS,
    ATTACK_LAND,
    ATTACK_ARMIES,
    GRANT_TITLES,
    TERMINATE_TITLES,
    JOIN_RETINUE,
    COLLECT_INFO_ABOUT_ARMIES,
    PROPOSE_MARRIAGE,
    PROPOSE_COUNCIL_ROLE,
    MOVE_ARMY_TO_LAND,
    IMPROVE_DEFENSE,
    MOVE_PLAYER_TO,
    REQUEST_LAND_REPORT,
    REQUEST_ARMY_LIST,
    REQUEST_ACTOR_LIST,
    TACTICAL_UNIT_ORDER,
    GENERAL_BATTLE_ORDER,
    MOVE_BATTLE_UNIT,
    HIRE_AGENT
}

enum class CouncilRoleProposal {
    CHANCELLOR,
    DEFENSE_COMMANDER,
    COIN_HOLDER
}

enum class DiplomaticOfferKind {
    ALLIANCE,
    PEACE,
    WAR
}

data class ResourceAmount(
    val code: String,
    val quantity: Int
)

data class MarriageProposal(
    val actorAId: Long,
    val actorBId: Long
)

data class CouncilRoleProposalTarget(
    val role: CouncilRoleProposal,
    val actorId: Long
)

data class MessageDraft(
    val messageType: MessageType,
    val orderKind: OrderKind,
    val lands: List<Long> = emptyList(),
    val structureIds: List<Long> = emptyList(),
    val actorIds: List<Long> = emptyList(),
    val resourceAmounts: List<ResourceAmount> = emptyList(),
    val countryIds: List<Long> = emptyList(),
    val armyIds: List<Long> = emptyList(),
    val enemyArmyIds: List<Long> = emptyList(),
    val titleIdsToGrant: List<Long> = emptyList(),
    val titleIdsToTerminate: List<Long> = emptyList(),
    val targetLandId: Long? = null,
    val targetActorId: Long? = null,
    val targetCountryId: Long? = null,
    val marriageProposal: MarriageProposal? = null,
    val councilRoleProposal: CouncilRoleProposalTarget? = null,
    val defenseStructureType: DefenseStructureType? = null,
    val scheduledTurn: Int? = null,
    val inviteLocalPeople: Boolean = false,
    val relatedEventId: Long? = null,
    val allowConvoyHire: Boolean = false,
    val freeTextNote: String? = null
)