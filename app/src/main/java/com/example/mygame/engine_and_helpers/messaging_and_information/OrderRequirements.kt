package com.example.mygame.engine_and_helpers.messaging_and_information

enum class DraftField {
    LANDS,
    STRUCTURES,
    ACTORS,
    RESOURCES,
    COUNTRIES,
    ARMIES,
    ENEMY_ARMIES,
    TITLES_TO_GRANT,
    TITLES_TO_TERMINATE,
    TARGET_LAND,
    TARGET_ACTOR,
    TARGET_COUNTRY,
    MARRIAGE_PROPOSAL,
    COUNCIL_ROLE_PROPOSAL,
    SCHEDULED_TURN,
    DEFENSE_STRUCTURE_TYPE
}

object OrderRequirements {

    fun requiredFields(kind: OrderKind): Set<DraftField> = when (kind) {
        OrderKind.COLLECT_TAX_FROM_LANDS ->
            setOf(DraftField.LANDS)

        OrderKind.COLLECT_TAX_FROM_ACTORS ->
            setOf(DraftField.ACTORS)

        OrderKind.IMPRISON_ACTORS,
        OrderKind.RELEASE_ACTORS,
        OrderKind.ORDER_KILL_ACTORS,
        OrderKind.COLLECT_INFO_ABOUT_ACTORS,
        OrderKind.INTERCEPT_CORRESPONDENCE ->
            setOf(DraftField.ACTORS)

        OrderKind.COLLECT_ARMY_IN_LANDS ->
            setOf(DraftField.LANDS)

        OrderKind.DISBAND_ARMIES,
        OrderKind.COLLECT_INFO_ABOUT_ARMIES ->
            setOf(DraftField.ARMIES)

        OrderKind.SELL_GOODS,
        OrderKind.BUY_GOODS ->
            setOf(DraftField.RESOURCES)

        OrderKind.CHECK_LOYALTY_LANDS ->
            setOf(DraftField.LANDS)

        OrderKind.CHECK_LOYALTY_ACTORS ->
            setOf(DraftField.ACTORS)

        OrderKind.ORGANIZE_REBELLION ->
            setOf(DraftField.TARGET_LAND)

        OrderKind.OFFER_ALLIANCE,
        OrderKind.OFFER_PEACE,
        OrderKind.DECLARE_WAR ->
            setOf(DraftField.TARGET_COUNTRY)

        OrderKind.INVITE_TO_BALL,
        OrderKind.INVITE_TO_FESTIVAL,
        OrderKind.INVITE_TO_RELIGIOUS_FESTIVAL,
        OrderKind.INVITE_TO_HUNT ->
            setOf(DraftField.ACTORS, DraftField.TARGET_LAND)
        OrderKind.ORGANIZE_BALL -> setOf(
            DraftField.TARGET_LAND,
            DraftField.STRUCTURES,
            DraftField.SCHEDULED_TURN
        )
        OrderKind.ORGANIZE_FESTIVAL,
        OrderKind.ORGANIZE_RELIGIOUS_FESTIVAL,
        OrderKind.ORGANIZE_HUNT ->
            setOf(DraftField.TARGET_LAND)

        OrderKind.CALL_FOR_DUEL ->
            setOf(DraftField.TARGET_ACTOR, DraftField.TARGET_LAND)

        OrderKind.COLLECT_INFO_ABOUT_LANDS ->
            setOf(DraftField.LANDS)

        OrderKind.ATTACK_LAND ->
            setOf(DraftField.ARMIES, DraftField.TARGET_LAND)

        OrderKind.ATTACK_ARMIES ->
            setOf(DraftField.ARMIES, DraftField.ENEMY_ARMIES)

        OrderKind.GRANT_TITLES ->
            setOf(DraftField.ACTORS, DraftField.TITLES_TO_GRANT)

        OrderKind.TERMINATE_TITLES ->
            setOf(DraftField.ACTORS, DraftField.TITLES_TO_TERMINATE)

        OrderKind.JOIN_RETINUE ->
            emptySet()

        OrderKind.PROPOSE_MARRIAGE ->
            setOf(DraftField.MARRIAGE_PROPOSAL)

        OrderKind.PROPOSE_COUNCIL_ROLE ->
            setOf(DraftField.COUNCIL_ROLE_PROPOSAL)

        OrderKind.IMPROVE_DEFENSE -> setOf(
            DraftField.TARGET_LAND,
            DraftField.DEFENSE_STRUCTURE_TYPE
        )

        OrderKind.MOVE_ARMY_TO_LAND -> setOf(DraftField.TARGET_LAND)

        OrderKind.MOVE_PLAYER_TO -> setOf(DraftField.TARGET_LAND)

        OrderKind.REQUEST_LAND_REPORT -> setOf(DraftField.LANDS)

        OrderKind.REQUEST_ARMY_LIST -> setOf(DraftField.LANDS)

        OrderKind.REQUEST_ACTOR_LIST -> setOf(DraftField.LANDS)

        OrderKind.TACTICAL_UNIT_ORDER -> setOf()

        OrderKind.HIRE_AGENT -> setOf()

        OrderKind.GENERAL_BATTLE_ORDER -> setOf()

        OrderKind.MOVE_BATTLE_UNIT -> setOf()

        OrderKind.TRANSPORT_RESOURCES,
        OrderKind.TRANSPORT_MONEY,
        OrderKind.BUY_ALCOHOL_FOR_EVENT,
        OrderKind.HIRE_BARDS_FOR_EVENT -> setOf()
    }
}