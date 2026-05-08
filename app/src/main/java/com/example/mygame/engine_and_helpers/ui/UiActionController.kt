package com.example.mygame.engine_and_helpers.ui

import com.example.mygame.database.map.ActorView
import com.example.mygame.database.map.MapMenuItem
import com.example.mygame.database.messaging_and_information.DeliveryMethod
import com.example.mygame.database.messaging_and_information.FreshInfoRequest
import com.example.mygame.database.messaging_and_information.MessageType
import com.example.mygame.database.persistence_and_game_state.CurrentSession
import com.example.mygame.database.politics_diplomacy_succession.FactionId
import com.example.mygame.engine_and_helpers.GameEngine
import com.example.mygame.engine_and_helpers.entertainment_and_social.AttendeeCandidate
import com.example.mygame.engine_and_helpers.entertainment_and_social.BallPlanDraft
import com.example.mygame.engine_and_helpers.messaging_and_information.DefenseStructureType
import com.example.mygame.engine_and_helpers.messaging_and_information.MessageDraft
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class UiActionController @Inject constructor(
    private val navigator: UiNavigator,
    private val shell: AppShell,
    private val session: CurrentSession,
    private val game: GameEngine
) {

    // =============================
    // View 1 / Room or Tent
    // =============================
    suspend fun onClickMapFromRoom() {
        navigator.goTo(ViewId.V2_WORLD_MAP, mapOf("landId" to session.currentLandId))
    }

    suspend fun onClickNobleReport(role: String, via: DeliveryMethod) {
        game.requestNobleReport(role, via)
        navigator.goTo(ViewId.V9_AUDIENCE, mapOf("role" to role))
    }

    suspend fun onClickEndTurn() {
        val res = game.endTurn()
        if (res.battleAlertAtMyLand) {
            navigator.goTo(ViewId.V3_BATTLE_ROOM_OR_TENT, mapOf("landId" to session.currentLandId))
        }
    }

    suspend fun onClickSkipTurns(count: Int) {
        val interrupted = game.skipTurnsUntilImportant(count)
        if (interrupted?.battleAlertAtMyLand == true) {
            navigator.goTo(ViewId.V3_BATTLE_ROOM_OR_TENT, mapOf("landId" to session.currentLandId))
        }
    }

    fun onClickReturnToMainMenu() {
        navigator.goTo(ViewId.V12_MAIN_MENU)
    }

    // =============================
    // View 2 / World Map
    // =============================

    suspend fun onOrganizeBallHere() = onCommandOrganizeBall(session.currentLandId, via = DeliveryMethod.LOCAL)
    suspend fun onOrganizeFestivalHere( isReligious: Boolean) = game.organizeFestival(session.currentLandId, religious = isReligious, via = DeliveryMethod.LOCAL)
    suspend fun onOrganizeHuntHere() = onCommandOrganizeHunt(session.currentLandId, DeliveryMethod.LOCAL)

    suspend fun onCommandOrganizeRebellion(landId: Long, via: DeliveryMethod = DeliveryMethod.MESSENGER) {
        game.organizeRebellion(landId, via)
    }

    suspend fun onCommandOrganizeBall(landId: Long, via: DeliveryMethod = DeliveryMethod.MESSENGER) {
        game.organizeBall(landId, via)
    }

    suspend fun onCommandOrganizeHunt(landId: Long, via: DeliveryMethod = DeliveryMethod.MESSENGER) {
        game.organizeHunt(landId, via)
    }

    // =============================
    // View 3 / Battle Room (or Tent)
    // =============================
    suspend fun onBattleSendOrderToUnit(unitId: Long, tactic: String, via: DeliveryMethod) {
        game.battleSendOrderToUnit(unitId, tactic, via)
    }

    suspend fun onBattleGeneralCommand(command: String, via: DeliveryMethod) {
        game.battleGeneralCommand(command, via)
    }

    suspend fun onBattleEndTurn() = onClickEndTurn()
    suspend fun onBattleSkipTurns(n: Int) = onClickSkipTurns(n)

    // =============================
    // View 4 / Battlefield map
    // =============================
    suspend fun onBattleMapMoveUnit(unitId: Long, toX: Int, toY: Int, via: DeliveryMethod) {
        game.battleMapMoveUnit(unitId, toX, toY, via)
        navigator.goTo(ViewId.V3_BATTLE_ROOM_OR_TENT)
    }

    // =============================
    // View 5 / Duel
    // =============================
    suspend fun onDuelStart(duelId: Long, opponentId: Long) {
        val outcome = game.duelStart(duelId, opponentId)
        if (outcome.playerDefeated) {
            navigator.goTo(ViewId.V11_DEFEAT)
        } else {
            navigator.goTo(ViewId.V1_ROOM_OR_TENT)
        }
    }

    suspend fun onDuelTryToAvoid(duelId: Long, opponentId: Long) {
        val avoidedOrWon = game.duelTryToAvoid(duelId, opponentId)
        if (avoidedOrWon) {
            navigator.goTo(ViewId.V1_ROOM_OR_TENT)
        } else {
            onDuelStart(duelId, opponentId)
        }
    }

    // =============================
    // View 6..16 (examples)
    // =============================
    suspend fun onBallJoin(ballId: Long) {
        game.ballJoin(ballId)
    }

    suspend fun onBallInviteToDance(targetId: Long) {
        game.ballInviteToDance(targetId)
    }

    suspend fun onBallReturnHome() {
        navigator.goTo(ViewId.V1_ROOM_OR_TENT)
    }

    suspend fun onHuntJoin(huntId: Long) {
        val r = game.huntJoin(huntId)
        if (r.playerDied) navigator.goTo(ViewId.V11_DEFEAT)
    }

    suspend fun onHuntReturnHome() { navigator.goTo(ViewId.V1_ROOM_OR_TENT) }

    suspend fun onFestivalJoin(festivalId: Long, religious: Boolean = false) {
        game.festivalJoin(festivalId, religious)
    }

    suspend fun onFestivalReturnHome() { navigator.goTo(ViewId.V1_ROOM_OR_TENT) }

    suspend fun onAudienceEnd() { navigator.goTo(ViewId.V1_ROOM_OR_TENT) }

    fun onStatsReturnToMenu() { navigator.goTo(ViewId.V12_MAIN_MENU) }

    suspend fun onMenuResumeGame() {
        navigator.goTo(ViewId.V1_ROOM_OR_TENT, mapOf("landId" to session.currentLandId))
    }

    suspend fun onMenuStartNewGame(scenarioId: Long, confirmDrop: Boolean) {
        val ok = game.startNewGameFromMenu(scenarioId, confirmDrop)
        if (ok) navigator.goTo(ViewId.V1_ROOM_OR_TENT)
    }

    fun onMenuExit() { shell.exitApp() }

    suspend fun onTavernShow(type: String) { game.tavernShow(type) }
    suspend fun onTavernHire(type: String, targetId: Long?) { game.tavernHire(type, targetId) }
    suspend fun onTavernBuyFood(item: String, qty: Int) { game.tavernBuyFood(item, qty) }
    suspend fun onTavernReturnHome() { navigator.goTo(ViewId.V1_ROOM_OR_TENT) }

    suspend fun onJudgeVerdict(crimeId: Long, guilty: Boolean) {
        game.judgeVerdict(crimeId, guilty)
        navigator.goTo(ViewId.V1_ROOM_OR_TENT)
    }

    suspend fun onArrestAttemptBribeJudge(amount: Int): Boolean = game.arrestAttemptBribeJudge(amount)
    suspend fun onArrestAttemptBribeWitness(witnessId: Long, amount: Int): Boolean = game.arrestAttemptBribeWitness(witnessId, amount)

    suspend fun onArrestVerdictReceived(guilty: Boolean, punishment: String?) {
        if (game.onArrestVerdictReceived(guilty, punishment)) {
            navigator.goTo(ViewId.V11_DEFEAT)
        } else {
            navigator.goTo(ViewId.V1_ROOM_OR_TENT)
        }
    }

    suspend fun onMealStart(menu: List<String>) {
        val poisoned = game.mealStart(menu)
        if (poisoned) navigator.goTo(ViewId.V11_DEFEAT)
    }

    suspend fun onMealEnd() { navigator.goTo(ViewId.V1_ROOM_OR_TENT) }

    suspend fun getGlobalMapSnapshot(w: Int, h: Int) = game.getGlobalMapSnapshot(w, h)

    // Called by SecondView when user taps a land or a flag
    suspend fun onMapItemPressed(landId: Long, faction: FactionId?) : List<MapMenuItem> =
        game.getMapMenuFor(landId, faction)

    // Below are tiny wrappers that call engine and optionally navigate
    suspend fun onChooseReport(
        landId: Long,
        autoconfirm: Boolean
    ) = game.getKnownLandReport(landId, autoconfirm)

    suspend fun onRefreshReportLocally(landId: Long) =
        game.refreshLandReportLocally(landId)

    suspend fun onRequestFreshReport(
        landId: Long,
        request: FreshInfoRequest
    ) {
        game.requestFreshLandReport(
            landId = landId,
            messageType = request.messageType,
            via = request.deliveryMethod
        )
    }

    suspend fun onChooseListArmies(
        landId: Long,
        autoconfirm: Boolean
    ) = game.getKnownArmyList(landId, autoconfirm)

    suspend fun onRefreshArmyListLocally(landId: Long) =
        game.refreshArmyListLocally(landId)

    suspend fun onRequestFreshArmyList(
        landId: Long,
        request: FreshInfoRequest
    ) {
        game.requestFreshArmyList(
            landId = landId,
            messageType = request.messageType,
            via = request.deliveryMethod
        )
    }

    suspend fun onChooseMoveArmyHere(armyId: Long, landId: Long, via: DeliveryMethod) {
        game.orderMoveArmy(armyId, landId, via)
    }

    suspend fun onChooseMovePlayerHere(landId: Long) {
        game.movePlayerTo(landId)
        navigator.goTo(ViewId.V1_ROOM_OR_TENT, mapOf("landId" to landId))
    }

    suspend fun onChooseImproveDefense(
        landId: Long,
        structureType: DefenseStructureType,
        via: DeliveryMethod
    ) {
        game.improveDefenseCommand(landId, structureType, via)
    }

    suspend fun onGetBallPlanDraft(ballId: Long) =
        game.getBallPlanDraft(ballId)

    suspend fun onCanUseLocalMusician(landId: Long) =
        game.canUseLocalMusician(landId)

    suspend fun onMarkBallUsesLocalMusician(ballId: Long) =
        game.markBallUsesLocalMusician(ballId)


    suspend fun onChooseOrganizeBall(landId: Long, via: DeliveryMethod) { game.organizeBall(landId, via) }
    suspend fun onChooseOrganizeHunt(landId: Long, via: DeliveryMethod) { game.organizeHunt(landId, via) }
    suspend fun onChooseOrganizeFestival(landId: Long, via: DeliveryMethod, religious: Boolean) {
        game.organizeFestival(landId, religious, via)
    }

    suspend fun onChooseListActors(
        landId: Long,
        autoconfirm: Boolean
    ) = game.getKnownActorList(landId, autoconfirm)

    suspend fun onRefreshActorListLocally(landId: Long) =
        game.refreshActorListLocally(landId)

    suspend fun onRequestFreshActorList(
        landId: Long,
        request: FreshInfoRequest
    ) {
        game.requestFreshActorsAtLand(
            landId = landId,
            messageType = request.messageType,
            via = request.deliveryMethod
        )
    }

    suspend fun onCreateLocalMusician(landId: Long) =
        game.createLocalMusicianIfNeeded(landId)

    fun isPlayerInLand(landId: Long): Boolean {
        return session.currentLandId == landId
    }

    suspend fun getKnownArmiesInLand(landId: Long) =
        game.getKnownArmiesInLand(landId)

    suspend fun onSendStructuredOrder(
        toRole: String?,
        toActorId: Long?,
        draft: MessageDraft,
        delivery: DeliveryMethod,
        sealed: Boolean
    ) = game.sendStructuredOrder(
        toRole = toRole,
        toActorId = toActorId,
        draft = draft,
        delivery = delivery,
        sealed = sealed
    )

    fun onGetDefaultBardFeeForNotability(notabilityLevel: Int): Int =
        game.getDefaultBardFeeForNotability(notabilityLevel)

    suspend fun onCanOrganizeBallInLand(
        landId: Long,
        orderedByActorId: Long
    ): Boolean = game.canOrganizeBallInLand(landId, orderedByActorId)

    suspend fun onGetCurrentChancellorActorId(): Long? =
        game.getCurrentChancellorActorId()

    fun onGetCurrentActorId(): Long = session.actorId

    suspend fun onGetLocalInviteeSuggestions(
        landId: Long,
        minimumTitleRank: Int
    ): List<AttendeeCandidate> =
        game.getLocalInviteeSuggestions(
            landId = landId,
            minimumTitleRank = minimumTitleRank
        )

    suspend fun onGetLocalInviteeSuggestions(
        landId: Long,
        minimumTitleRank: Int,
        organizerActorId: Long
    ): List<AttendeeCandidate> =
        game.getLocalInviteeSuggestions(
            landId = landId,
            minimumTitleRank = minimumTitleRank,
            organizerActorId = organizerActorId
        )

    suspend fun onGetRemoteInviteeSuggestions(
        targetLandId: Long
    ): List<AttendeeCandidate> =
        game.getRemoteInviteeSuggestions(
            targetLandId = targetLandId
        )

    suspend fun onGetRemoteInviteeSuggestions(
        targetLandId: Long,
        organizerActorId: Long
    ): List<AttendeeCandidate> =
        game.getRemoteInviteeSuggestions(
            targetLandId = targetLandId,
            organizerActorId = organizerActorId
        )

    suspend fun onGetLandName(landId: Long?): String? =
        game.getLandName(landId)

    fun onPreviewBallBudget(draft: BallPlanDraft) =
        game.previewBallBudget(draft)

    suspend fun onGetAvailableDeliveryCarriersForLand(landId: Long): Pair<Int, Int> {
        return game.getAvailableDeliveryCarriersForLand(landId)
    }

    fun onGetRecommendedAlcoholUnitsForBall(
        attendeeCount: Int,
        inviteLocalPeople: Boolean
    ) = game.getRecommendedAlcoholUnitsForBall(attendeeCount, inviteLocalPeople)

    suspend fun onRequestMerchantToBuyAlcohol(
        ballId: Long,
        delivery: DeliveryMethod = DeliveryMethod.LOCAL
    ) = game.requestMerchantToBuyAlcohol(ballId, delivery)

    fun onGetDefaultBardFeeSilver(): Int =
        game.getDefaultBardFeeSilver()

    suspend fun onGetBallPlanningOptions(landId: Long) =
        game.getBallPlanningOptions(landId)

    suspend fun onGetBallPlanningOptions(
        landId: Long,
        organizerActorId: Long
    ) = game.getBallPlanningOptions(
        targetLandId = landId,
        orderedByActorId = organizerActorId,
        organizerActorId = organizerActorId
    )

    suspend fun onGetBallPlanningOptions(
        landId: Long,
        orderedByActorId: Long,
        organizerActorId: Long
    ) = game.getBallPlanningOptions(
        targetLandId = landId,
        orderedByActorId = orderedByActorId,
        organizerActorId = organizerActorId
    )

    suspend fun onPreviewBallPlan(draft: BallPlanDraft) =
        game.previewBallPlan(draft)

    suspend fun onExecuteManualBallPlan(
        draft: BallPlanDraft,
        deliveryForRemoteContacts: DeliveryMethod,
        generalInvitationText: String? = null
    ) = game.executeManualBallPlan(draft, deliveryForRemoteContacts, generalInvitationText)

    suspend fun onSuggestBallSendLater(
        draft: BallPlanDraft
    ) = game.validateBallDeliveryCapacity(draft)

    suspend fun onValidateBallDeliveryCapacity(draft: BallPlanDraft) =
        game.validateBallDeliveryCapacity(draft)

    suspend fun onGetCurrentCoinHolderActorId(): Long? =
        game.getCurrentCoinHolderActorId()

    suspend fun onGetCurrentCoinHolderActorId(
        viewerActorId: Long
    ): Long? = game.getCurrentCoinHolderActorId(viewerActorId)

    suspend fun onGetKnownActorLand(
        viewerActorId: Long,
        actorId: Long
    ): Long? = game.getKnownActorLandForViewer(
        viewerActorId = viewerActorId,
        actorId = actorId
    )

    suspend fun onSendBallMessagesNow(ballId: Long): GameEngine.BallMessageSendResult {
        return game.sendBallMessagesNow(ballId)
    }

    suspend fun onGetBallTitleRankOptions() =
        game.getBallTitleRankOptions()

    suspend fun onGetActiveBallPlanForLand(landId: Long) =
        game.getActiveBallPlanForLand(landId)

    suspend fun onSaveBallPlanDraft(
        draft: BallPlanDraft,
        existingBallId: Long?
    ) = game.saveBallPlanDraft(draft, existingBallId)

    suspend fun onCancelBallPlan(ballId: Long) =
        game.cancelBallPlan(ballId)

    suspend fun onFinalizeBallPlan(
        ballId: Long,
        deliveryForRemoteContacts: DeliveryMethod? = null
    ) = game.finalizeBallPlan(ballId, deliveryForRemoteContacts)

    suspend fun onGetActualActorLand(actorId: Long): Long? =
        game.getActualActorLand(actorId)

    fun onGetRecommendedBardCount(invitedParticipantCount: Int): Int =
        game.getRecommendedBardCount(invitedParticipantCount)

    fun onGetRecommendedInviteeCount(performerCount: Int): Int? =
        game.getRecommendedInviteeCount(performerCount)
}