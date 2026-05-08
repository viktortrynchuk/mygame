package com.example.mygame.engine_and_helpers

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.example.mygame.R
import com.example.mygame.dao.ActorDAO
import com.example.mygame.dao.ActorLocationDAO
import com.example.mygame.dao.SessionDAO
import com.example.mygame.dao.armies_units_warfare.PrisonerDao
import com.example.mygame.dao.economy_resources_trade.TreasuryDao
import com.example.mygame.dao.entertainment_and_social.BallDao
import com.example.mygame.dao.entertainment_and_social.GossipDao
import com.example.mygame.dao.entertainment_and_social.PlannedBallAlcoholDao
import com.example.mygame.dao.entertainment_and_social.PlannedBallBardDao
import com.example.mygame.dao.entertainment_and_social.PlannedBallDao
import com.example.mygame.dao.entertainment_and_social.PlannedBallGuestDao
import com.example.mygame.dao.foundations_core.AuditLogDao
import com.example.mygame.dao.messaging_and_information.KnowledgeDao
import com.example.mygame.dao.nobility_titles_and_court.NobleDao
import com.example.mygame.dao.persistence_and_game_state.ScenarioDao
import com.example.mygame.dao.world_and_geography.LandDao
import com.example.mygame.dao.world_and_geography.OwnershipDao
import com.example.mygame.dao.world_and_geography.isOwnedByPlayerOrVassal
import com.example.mygame.database.entertainment_and_social.BallEventEntity
import com.example.mygame.database.entertainment_and_social.BallPreparationState
import com.example.mygame.database.entertainment_and_social.PlannedBallAlcoholEntity
import com.example.mygame.database.entertainment_and_social.PlannedBallBardEntity
import com.example.mygame.database.entertainment_and_social.PlannedBallEntity
import com.example.mygame.database.entertainment_and_social.PlannedBallGuestEntity
import com.example.mygame.database.map.ActorView
import com.example.mygame.database.map.FlagKind
import com.example.mygame.database.map.FlagMarker
import com.example.mygame.database.map.MapMenuAction
import com.example.mygame.database.map.MapMenuItem
import com.example.mygame.database.map.MapSnapshot
import com.example.mygame.database.map.SettlementMarker
import com.example.mygame.database.messaging_and_information.ActorListActionState
import com.example.mygame.database.messaging_and_information.ArmyInfo
import com.example.mygame.database.messaging_and_information.ArmyKnowledge
import com.example.mygame.database.messaging_and_information.ArmyListActionState
import com.example.mygame.database.messaging_and_information.KnowledgeEntryEntity
import com.example.mygame.database.messaging_and_information.KnownActorAtLand
import com.example.mygame.database.messaging_and_information.LandReportActionState
import com.example.mygame.database.messaging_and_information.MessageComposerState
import com.example.mygame.database.messaging_and_information.MessageEntity
import com.example.mygame.database.messaging_and_information.MessageType
import com.example.mygame.database.messaging_and_information.ReportResult
import com.example.mygame.database.persistence_and_game_state.CurrentSession
import com.example.mygame.database.persistence_and_game_state.ScenarioEntity
import com.example.mygame.database.politics_diplomacy_succession.FactionId
import com.example.mygame.engine_and_helpers.dignity_duels_conflicts.DignityService
import com.example.mygame.engine_and_helpers.dignity_duels_conflicts.DuelOddsCalculator
import com.example.mygame.engine_and_helpers.entertainment_and_social.AlcoholStockOption
import com.example.mygame.engine_and_helpers.entertainment_and_social.AttendeeCandidate
import com.example.mygame.engine_and_helpers.entertainment_and_social.BallAlcoholPlan
import com.example.mygame.engine_and_helpers.entertainment_and_social.BallPlanDraft
import com.example.mygame.engine_and_helpers.entertainment_and_social.BallPlanningOptions
import com.example.mygame.engine_and_helpers.entertainment_and_social.BardCandidate
import com.example.mygame.engine_and_helpers.entertainment_and_social.EntertainmentImpactCalculator
import com.example.mygame.engine_and_helpers.entertainment_and_social.MarketAlcoholOption
import com.example.mygame.engine_and_helpers.entertainment_and_social.MarketBuy
import com.example.mygame.engine_and_helpers.entertainment_and_social.SocialService
import com.example.mygame.engine_and_helpers.entertainment_and_social.StockUse
import com.example.mygame.engine_and_helpers.entertainment_and_social.VenueOption
import com.example.mygame.engine_and_helpers.foundations_core.AuditLogger
import com.example.mygame.engine_and_helpers.foundations_core.TurnClockService
import com.example.mygame.engine_and_helpers.justice_and_court.JusticeService
import com.example.mygame.engine_and_helpers.map.ArmySpeedProvider
import com.example.mygame.engine_and_helpers.map.KnowledgeRepo
import com.example.mygame.engine_and_helpers.map.MapLayoutProvider
import com.example.mygame.engine_and_helpers.messaging_and_information.DefenseStructureType
import com.example.mygame.engine_and_helpers.messaging_and_information.MessageDraft
import com.example.mygame.engine_and_helpers.messaging_and_information.OrderComposer
import com.example.mygame.engine_and_helpers.messaging_and_information.OrderKind
import com.example.mygame.engine_and_helpers.messaging_and_information.OrderRequirements
import com.example.mygame.engine_and_helpers.messaging_and_information.PostOffice
import com.example.mygame.engine_and_helpers.messaging_and_information.ReportIngestion
import com.example.mygame.engine_and_helpers.messaging_and_information.ResourceAmount
import com.example.mygame.engine_and_helpers.persistence_and_game_state.CommitService
import com.example.mygame.engine_and_helpers.test_seeder.DbSeeder
import com.example.mygame.engine_and_helpers.ui.DefaultRandom
import com.example.mygame.engine_and_helpers.ui.RandomProvider
import com.example.mygame.views.OptionUi
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.max
import com.example.mygame.dao.movements_logistics_supplies.ConvoyDao
import com.example.mygame.dao.movements_logistics_supplies.WagonDao
import com.example.mygame.dao.messaging_and_information.MessengerDao
import com.example.mygame.dao.messaging_and_information.DoveDao
import com.example.mygame.dao.messaging_and_information.MessageDao
import com.example.mygame.dao.messaging_and_information.PostOfficeDao
import com.example.mygame.dao.movements_logistics_supplies.MovementOrderDao
import com.example.mygame.dao.nobility_titles_and_court.NobleTitleDao
import com.example.mygame.dao.nobility_titles_and_court.PrestigeDao
import com.example.mygame.dao.nobility_titles_and_court.TitleDao
import com.example.mygame.dao.world_and_geography.StructureDao
import com.example.mygame.database.Actor
import com.example.mygame.database.ActorLocation
import com.example.mygame.database.messaging_and_information.DeliveryMethod
import com.example.mygame.database.movements_logistics_supplies.ConvoyEntity
import com.example.mygame.database.movements_logistics_supplies.MovementOrderEntity
import com.example.mygame.database.movements_logistics_supplies.WagonEntity
import com.example.mygame.database.world_and_geography.StructureEntity
import com.example.mygame.engine_and_helpers.entertainment_and_social.BardPlanItem
import com.example.mygame.engine_and_helpers.entertainment_and_social.GuestPlanItem
import kotlin.random.Random
import kotlin.math.max
import com.example.mygame.dao.population_and_society.MayorDao
import com.example.mygame.database.entertainment_and_social.GossipEntity
import com.example.mygame.database.nobility_titles_and_court.PrestigeLogEntity

@ActivityScoped
class GameEngine @Inject constructor(
    @ActivityContext private val ctx: Context,
// Persistence for start/load
    private val scenarioDao: ScenarioDao,
    private val sessionDao: SessionDAO,
    private val dbSeeder: DbSeeder,

    // Core game services
    private val turns: TurnClockService,
    private val commits: CommitService,
    private val audit: AuditLogger,
    private val post: PostOffice,
    private val ingestion: ReportIngestion,

    // Domain/gameplay helpers
    private val dignity: DignityService,
    private val duelOdds: DuelOddsCalculator,
    private val social: SocialService,
    private val entertainImpact: EntertainmentImpactCalculator,
    private val justice: JusticeService,

    // Session & RNG
    private val session: CurrentSession,
    private val rng: RandomProvider = DefaultRandom(),
    private val layout: MapLayoutProvider,
    private val speeds: ArmySpeedProvider,
    private val knowledge: KnowledgeRepo,
    private val LandDao: LandDao,
    private val OwnershipDao: OwnershipDao,
    private val knowledgeDao: KnowledgeDao,
    private val actorDao: ActorDAO,
    private val actorLocationDao: ActorLocationDAO,
    private val treasuryDao: TreasuryDao,
    private val structureDao: StructureDao,
    private val messageDao: MessageDao,

    //Ball
    private val plannedBallDao: PlannedBallDao,
    private val plannedBallBardDao: PlannedBallBardDao,
    private val plannedBallGuestDao: PlannedBallGuestDao,
    private val plannedBallAlcoholDao: PlannedBallAlcoholDao,
    private val ballDao: BallDao,

    private val convoyDao: ConvoyDao,
    private val wagonDao: WagonDao,
    private val messengerDao: MessengerDao,
    private val doveDao: DoveDao,
    private val postOfficeDao: PostOfficeDao,
    private val nobleTitleDao: NobleTitleDao,
    private val titleDao: TitleDao,
    private val mayorDao: MayorDao,

    private val gossipDao: GossipDao,
    private val prestigeDao: PrestigeDao,
    private val auditLogDao: AuditLogDao,

    private val movementOrderDao: MovementOrderDao,

    ) {
    private companion object {
        const val MONEY_CODE = "SILVER"
        const val DEFAULT_BARD_FEE_SILVER = 10
        const val MERCHANT_SALARY_PERCENT = 10
        const val MIN_MERCHANT_SALARY_SILVER = 1

        const val BALL_SCALE_LITTLE = "LITTLE"
        const val BALL_SCALE_MIDDLE = "MIDDLE"
        const val BALL_SCALE_BIG = "BIG"

        const val BALL_SCALE_LITTLE_BUDGET = 50
        const val BALL_SCALE_MIDDLE_BUDGET = 100
        const val BALL_SCALE_BIG_BUDGET = 200

        const val MAX_PERSONAL_DELIVERIES_PER_TURN = 10
    }

    data class BallPlanPreview(
        val targetLandId: Long,
        val eventTurn: Int,
        val bardActorIds: List<Long>,
        val attendeeActorIds: List<Long>,
        val inviteLocalPeople: Boolean,
        val venueStructureId: Long,
        val alcoholPlan: BallAlcoholPlan,
        val missing: List<String>
    ) {
        val isReady: Boolean get() = missing.isEmpty()
    }

    data class BallBudgetPreview(
        val baseBudget: Int,
        val bardSubtotal: Int,
        val wineSubtotal: Int,
        val totalBudget: Int
    )

    data class BallDeliveryCapacityResult(
        val ok: Boolean,
        val requiredMessengers: Int,
        val availableMessengers: Int,
        val requiredDoves: Int,
        val availableDoves: Int,
        val message: String? = null
    )

    data class TitleRankOption(
        val rank: Int,
        val titleId: Long,
        val titleName: String
    )

    private val localBardNames = listOf(
        "Andrew", "Thomas", "Peter", "Hugh", "Robert", "Martin", "Simon", "Geoffrey"
    )

    sealed class BallFinalizeResult {
        data class Success(val message: String) : BallFinalizeResult()
        data class Blocked(val message: String) : BallFinalizeResult()
    }

    sealed class BallMessageSendResult {
        data class Success(
            val sentCount: Int,
            val sentMessages: List<String> = emptyList(),
            val skippedMessages: List<String> = emptyList()
        ) : BallMessageSendResult()

        data class Blocked(
            val message: String
        ) : BallMessageSendResult()
    }

    // We need a FragmentActivity for the picker
    private val activity: FragmentActivity
        get() = ctx as FragmentActivity

    private val picker = OptionPickEngine(activity)

    fun getDefaultBardFeeSilver(): Int = DEFAULT_BARD_FEE_SILVER

    fun startGameAsync(
        scope: CoroutineScope,
        onResult: (CurrentSession) -> Unit
    ) {
        scope.launch { onResult(startGame()) }
    }

    fun loadGameAsync(
        scope: CoroutineScope,
        onResult: (CurrentSession) -> Unit
    ) {
        scope.launch { onResult(loadGame()) }
    }

    /** Return existing session if any, otherwise ask the user to pick a scenario */
    suspend fun loadGame(): CurrentSession = getExistingSessionOrAskUser()

    /** Always ask user to pick a scenario and create a new session */
    suspend fun startGame(): CurrentSession = askUser()

    private suspend fun getExistingSessionOrAskUser(): CurrentSession {
        val existing = getExistingSession()
        return existing ?: askUser()
    }

    /** Ask user to choose a scenario via OptionPickEngine and create a session */
    private suspend fun askUser(): CurrentSession {
        // 1) Ensure scenarios exist and fetch them
        val scenarios: List<ScenarioEntity> = fetchScenariosEnsuringSeed()

        // 2) Map to UI options once
        val ui: List<OptionUi> = scenarios.map { s -> OptionUi(id = s.scenarioId, title = s.descr) }

        // 3) Show picker
        val result = picker.pickOptions(
            optionsProvider = { ui },
            showGuardOnConfirm = true,
            touchGuardId = R.id.touchGuard
        ) ?: return Constants.emptySession

        // 4) Resolve chosen scenario
        val chosen = scenarios.firstOrNull { it.scenarioId == result.id }
            ?: scenarios.getOrNull(result.position)
            ?: return Constants.emptySession

        // 5) Reset DB from scenario source and get the freshly re-seeded scenario row
        val preparedScenario = prepareDBforNewGame(chosen)

        // 6) Create session after reseed
        val created: CurrentSession = withContext(Dispatchers.IO) {
            sessionDao.deleteAll()

            val newSession = CurrentSession(
                scenarioId = preparedScenario.scenarioId,
                actorId = preparedScenario.actorId,
                currentLandId = preparedScenario.currentLandId,
                startingView = preparedScenario.viewNum,
                currentTurn = 0
            )
            sessionDao.insertSession(newSession)
            newSession
        }

        // 7) Unfreeze UI
        picker.hideGuard(R.id.touchGuard)

        return created
    }

    /** Return first existing session or null */
    private suspend fun getExistingSession(): CurrentSession? = withContext(Dispatchers.IO) {
        sessionDao.getAllSessions().firstOrNull()
    }

    /** Ensure seed data exists; then return scenarios from DB */
    private suspend fun fetchScenariosEnsuringSeed(): List<ScenarioEntity> = withContext(Dispatchers.IO) {
        dbSeeder.seedAllIfEmpty()
        scenarioDao.getAllScenarios()
    }

    /** Clear only current sessions before inserting the freshly prepared one */
    private suspend fun deleteRuntimeDBTables() = withContext(Dispatchers.IO) {
        sessionDao.deleteAll()
    }

    /** Rebuild DB from seed/scenario source and return the freshly inserted chosen scenario row */
    private suspend fun prepareDBforNewGame(chosen: ScenarioEntity): ScenarioEntity {
        return dbSeeder.resetForNewGame(chosen.descr)
    }
// =============================
    // Reusable game flows (called by UiActionController)
    // =============================

    data class EndTurnResult(val newTurn: Int, val battleAlertAtMyLand: Boolean)

    suspend fun endTurn(): EndTurnResult {
        // 1) commit → 2) tick → 3) ingest
        commits.commitTurn()
        val next = turns.tick()

        processSameLandDeliveryMovementOrders()

        val ingested = ingestion.ingestReportsFor(session.actorId)
        audit.log("END_TURN", mapOf("turn" to next.turn, "ingested" to ingested))

        // 4) check for important inbox events
        val alert = importantEventInInbox()
        return EndTurnResult(newTurn = next.turn, battleAlertAtMyLand = alert?.payload?.startsWith("BATTLE_AT:") == true)
    }

    suspend fun skipTurnsUntilImportant(count: Int): EndTurnResult? {
        repeat(count) {
            val res = endTurn()
            if (res.battleAlertAtMyLand) return res
        }
        return null
    }

    private suspend fun importantEventInInbox(): MessageEntity? {
        val inbox = post.inbox(session.actorId)
        return inbox.lastOrNull { m ->
            val p = m.payload
            p.startsWith("DECLARE_WAR:")
                    || p.startsWith("DUEL_REQUEST:")
                    || p.startsWith("BATTLE_AT:${session.currentLandId}")
                    || p.startsWith("ASSASSINATION_ALERT:")
        }
    }

    private suspend fun processSameLandDeliveryMovementOrders() {
        val currentTurn = turns.read().turn

        val readySameLandDeliveries = movementOrderDao.getActive()
            .filter { order ->
                order.messageId != null &&
                        order.sourceLandId != null &&
                        order.targetLandId != null &&
                        order.sourceLandId == order.targetLandId &&
                        order.createdTurn < currentTurn &&
                        (order.carrierType == "MESSENGER" || order.carrierType == "DOVE")
            }

        readySameLandDeliveries.forEach { order ->
            val message = messageDao.byId(order.messageId ?: return@forEach)
            if (message == null) {
                movementOrderDao.update(order.copy(status = "FAILED_MESSAGE_NOT_FOUND"))
                return@forEach
            }

            val recipientActorId = message.toActorId
            val targetLandId = order.targetLandId

            if (recipientActorId != null && targetLandId != null) {
                val targetActuallyHere = actualActorLand(recipientActorId) == targetLandId

                if (!targetActuallyHere) {
                    invalidateKnownActorLocationIfObservedAbsent(
                        viewerActorId = message.fromActorId,
                        targetActorId = recipientActorId,
                        observedLandId = targetLandId
                    )

                    audit.log(
                        "MESSAGE_RECIPIENT_ABSENT",
                        mapOf(
                            "messageId" to message.id,
                            "fromActorId" to message.fromActorId,
                            "toActorId" to recipientActorId,
                            "targetLandId" to targetLandId,
                            "carrierType" to order.carrierType,
                            "turn" to currentTurn
                        )
                    )

                    movementOrderDao.update(order.copy(status = "FAILED_RECIPIENT_ABSENT"))
                    return@forEach
                }
            }

            movementOrderDao.update(order.copy(status = "DELIVERED"))
        }
    }

    private suspend fun requireMayorActorIdForLand(landId: Long): Long {
        return mayorDao.get(landId)?.actorId
            ?: error("No mayor actor is registered for land $landId.")
    }

    // ----- requests / orders from various views -----
    private fun resolveInvitationMessageType(
        oral: Boolean,
        sealedLetter: Boolean
    ): MessageType {
        return when {
            oral -> MessageType.ORAL
            sealedLetter -> MessageType.SEALED_LETTER
            else -> MessageType.LETTER
        }
    }

    private fun isMessageSealed(messageType: MessageType): Boolean {
        return messageType == MessageType.SEALED_LETTER
    }

    private fun normalizeInvitationMessageTypeForLocal(
        sourceLandId: Long,
        targetLandId: Long,
        requestedType: MessageType
    ): MessageType {
        return requestedType
    }

    fun getBallScaleOptions(): List<String> = listOf(
        BALL_SCALE_LITTLE,
        BALL_SCALE_MIDDLE,
        BALL_SCALE_BIG
    )

    private fun normalizeBallScale(scale: String?): String? {
        return when (scale?.trim()?.uppercase()) {
            BALL_SCALE_LITTLE -> BALL_SCALE_LITTLE
            BALL_SCALE_MIDDLE -> BALL_SCALE_MIDDLE
            BALL_SCALE_BIG -> BALL_SCALE_BIG
            else -> null
        }
    }

    private fun isDelegatedBallDraft(draft: BallPlanDraft): Boolean {
        return draft.organizerActorId != draft.orderedByActorId
    }

    private fun isDelegatedPlannedBall(ball: PlannedBallEntity): Boolean {
        return ball.organizerActorId != ball.orderedByActorId
    }

    private fun getStandardBudgetForBallScale(scale: String?): Int {
        return when (normalizeBallScale(scale)) {
            BALL_SCALE_LITTLE -> BALL_SCALE_LITTLE_BUDGET
            BALL_SCALE_MIDDLE -> BALL_SCALE_MIDDLE_BUDGET
            BALL_SCALE_BIG -> BALL_SCALE_BIG_BUDGET
            else -> 0
        }
    }

    suspend fun getBallTitleRankOptions(): List<TitleRankOption> {
        return titleDao.list()
            .sortedBy { it.id }
            .mapIndexed { index, title ->
                TitleRankOption(
                    rank = index + 1,
                    titleId = title.id,
                    titleName = title.name
                )
            }
    }

    private suspend fun materializeLocalMusiciansIfNeeded(
        ballId: Long,
        landId: Long,
        localMusicianCount: Int
    ): List<PlannedBallBardEntity> {
        if (localMusicianCount <= 0) return emptyList()

        val existingLocalPlans = plannedBallBardDao.forBall(ballId)
            .filter { it.generatedLocally }

        val materializedLocalPlans = existingLocalPlans.map { existingPlan ->
            materializeLocalMusicianForBall(
                ballId = ballId,
                landId = landId,
                existingPlan = existingPlan
            )
        }

        val remainingCount = (localMusicianCount - materializedLocalPlans.size).coerceAtLeast(0)
        if (remainingCount <= 0) {
            return plannedBallBardDao.forBall(ballId).filter { it.generatedLocally }
        }

        val ball = plannedBallDao.byId(ballId)
        val currentMaxActorId = actorDao.actorsForScenario(session.scenarioId)
            .maxOfOrNull { it.actorId } ?: 0L

        val landName = LandDao.get(landId)?.name ?: "the Land"
        var nextActorId = currentMaxActorId + 1L

        repeat(remainingCount) { index ->
            val baseName = localBardNames.random(Random(landId + turns.read().turn + ballId + index))
            val actorName = "$baseName of $landName"

            val actor = Actor(
                scenarioId = session.scenarioId,
                actorId = nextActorId,
                name = actorName,
                actorType = "BARD",
                notabilityLevel = 1
            )
            actorDao.upsert(actor)

            actorLocationDao.upsert(
                ActorLocation(
                    scenarioId = session.scenarioId,
                    actorId = nextActorId,
                    locationId = landId
                )
            )

            plannedBallBardDao.upsert(
                PlannedBallBardEntity(
                    plannedBallId = ballId,
                    bardActorId = nextActorId,
                    invitationSent = false,
                    accepted = null,
                    deliveryMethod = DeliveryMethod.MESSENGER,
                    messageType = MessageType.SEALED_LETTER,
                    feeSilver = DEFAULT_BARD_FEE_SILVER,
                    notabilityLevel = 1,
                    generatedLocally = true,
                    sendLater = false
                )
            )

            publishActorKnowledgeForViewer(
                viewerActorId = ball?.orderedByActorId ?: session.actorId,
                actorId = nextActorId,
                actorName = actorName,
                landId = landId
            )

            if (ball != null && ball.organizerActorId != ball.orderedByActorId) {
                publishActorKnowledgeForViewer(
                    viewerActorId = ball.organizerActorId,
                    actorId = nextActorId,
                    actorName = actorName,
                    landId = landId
                )
            }

            nextActorId++
        }

        return plannedBallBardDao.forBall(ballId).filter { it.generatedLocally }
    }

    private suspend fun materializeLocalMusicianForBall(
        ballId: Long,
        landId: Long,
        existingPlan: PlannedBallBardEntity
    ): PlannedBallBardEntity {
        if (!existingPlan.generatedLocally) return existingPlan
        if (existingPlan.bardActorId > 0L) return existingPlan

        val ball = plannedBallDao.byId(ballId)
        val nextId = (actorDao.actorsForScenario(session.scenarioId).maxOfOrNull { it.actorId } ?: 0L) + 1L
        val landName = LandDao.get(landId)?.name ?: "the Land"
        val baseName = localBardNames.random(Random(landId + turns.read().turn + ballId + existingPlan.id))
        val actorName = "$baseName of $landName"

        val actor = Actor(
            scenarioId = session.scenarioId,
            actorId = nextId,
            name = actorName,
            actorType = "BARD",
            notabilityLevel = max(1, existingPlan.notabilityLevel)
        )
        actorDao.upsert(actor)

        actorLocationDao.upsert(
            ActorLocation(
                scenarioId = session.scenarioId,
                actorId = nextId,
                locationId = landId
            )
        )

        val updated = existingPlan.copy(
            bardActorId = nextId,
            invitationSent = false,
            accepted = null,
            deliveryMethod = DeliveryMethod.MESSENGER,
            messageType = MessageType.SEALED_LETTER,
            feeSilver = if (existingPlan.feeSilver > 0) existingPlan.feeSilver else DEFAULT_BARD_FEE_SILVER
        )
        plannedBallBardDao.upsert(updated)

        publishActorKnowledgeForViewer(
            viewerActorId = ball?.orderedByActorId ?: session.actorId,
            actorId = nextId,
            actorName = actorName,
            landId = landId
        )

        if (ball != null && ball.organizerActorId != ball.orderedByActorId) {
            publishActorKnowledgeForViewer(
                viewerActorId = ball.organizerActorId,
                actorId = nextId,
                actorName = actorName,
                landId = landId
            )
        }

        return updated
    }

    suspend fun getBallPlanningOptions(
        targetLandId: Long,
        orderedByActorId: Long = session.actorId,
        organizerActorId: Long = orderedByActorId
    ): BallPlanningOptions {
        val knowledgeActorId = if (organizerActorId != orderedByActorId) {
            orderedByActorId
        } else {
            organizerActorId
        }

        return BallPlanningOptions(
            possibleTurns = buildList {
                val now = turns.read().turn
                for (t in (now + 1)..(now + 12)) add(t)
            },
            bards = getKnownBards(knowledgeActorId),
            attendees = getKnownInviteeNobles(knowledgeActorId),
            venues = getEligibleVenueOptionsForRequestor(
                targetLandId = targetLandId,
                requestorActorId = orderedByActorId
            ),
            alcoholStocks = getKnownAlcoholStocks(knowledgeActorId).filter { it.code == "WINE" },
            marketAlcohol = getKnownAlcoholMarkets(
                targetLandId = targetLandId,
                knowerActorId = knowledgeActorId
            ).filter { it.code == "WINE" },
            budgetStocks = getKnownBudgetStocks(
                organizerActorId = knowledgeActorId,
                targetLandId = targetLandId
            )
        )
    }

    fun getRecommendedBardCount(invitedParticipantCount: Int): Int {
        return when {
            invitedParticipantCount <= 0 -> 1
            invitedParticipantCount <= 10 -> 1
            invitedParticipantCount <= 25 -> 2
            invitedParticipantCount <= 50 -> 3
            invitedParticipantCount <= 100 -> 4
            else -> 5
        }
    }

    fun getRecommendedInviteeCount(performerCount: Int): Int? {
        return when {
            performerCount <= 0 -> 0
            performerCount == 1 -> 12
            performerCount == 2 -> 30
            performerCount == 3 -> 60
            performerCount == 4 -> 120
            else -> null
        }
    }

    fun getRecommendedWineUnits(totalParticipants: Int): Int {
        if (totalParticipants <= 0) return 0
        return max(0, (totalParticipants * 2) / 3)
    }

    private suspend fun getKnownInviteeNobles(
        organizerActorId: Long
    ): List<AttendeeCandidate> {
        val knownActors = getKnownActorsAcrossLandsFromKnowledge(organizerActorId)
        val knownActorIds = knownActors.map { it.actorId }.toSet()

        if (knownActorIds.isEmpty()) return emptyList()

        val titleRanksByActor = mutableMapOf<Long, Int>()
        knownActorIds.forEach { actorId ->
            val highestRank = getHighestKnownTitleRank(actorId)
            if (highestRank > 0) {
                titleRanksByActor[actorId] = highestRank
            }
        }

        return knownActors
            .filter { it.actorId in titleRanksByActor.keys }
            .map {
                AttendeeCandidate(
                    actorId = it.actorId,
                    name = it.name ?: "Unknown #${it.actorId}",
                    lastKnownLandId = knownActorLand(it.actorId),
                    lastSeenTurn = it.lastSeenTurn,
                    imprisoned = it.imprisoned ?: false,
                    titleRank = titleRanksByActor[it.actorId] ?: 0
                )
            }
            .sortedBy { it.name }
    }

    private suspend fun getHighestKnownTitleRank(actorId: Long): Int {
        val nobleTitles = nobleTitleDao.byNoble(actorId)
        if (nobleTitles.isEmpty()) return 0

        val rankByTitleId = titleDao.list()
            .sortedBy { it.id }
            .mapIndexed { index, title -> title.id to (index + 1) }
            .toMap()

        return nobleTitles.maxOfOrNull { nobleTitle ->
            rankByTitleId[nobleTitle.titleId] ?: 0
        } ?: 0
    }

    suspend fun getLandName(landId: Long?): String? {
        if (landId == null) return null
        return LandDao.get(landId)?.name
    }

    private suspend fun getKnownActorsAcrossLandsFromKnowledge(
        knowerActorId: Long
    ): List<KnownActorAtLand> {
        val entries = knowledgeDao.getAllForActor(knowerActorId)

        val names = mutableMapOf<Long, String>()
        val latestAt = mutableMapOf<Long, Pair<Boolean?, Int>>()

        entries.sortedBy { it.turn }.forEach { entry ->
            parseActorNameFact(entry.fact)?.let { (actorId, name) ->
                names[actorId] = name
            }

            parseActorAtFact(entry.fact)?.let { (actorId, imprisoned) ->
                val prev = latestAt[actorId]
                if (prev == null || entry.turn >= prev.second) {
                    latestAt[actorId] = imprisoned to entry.turn
                }
            }
        }

        val actorIds = (names.keys + latestAt.keys).sorted()
        return actorIds.map { actorId ->
            val state = latestAt[actorId]
            KnownActorAtLand(
                actorId = actorId,
                name = names[actorId],
                imprisoned = state?.first,
                lastSeenTurn = state?.second ?: 0
            )
        }
    }

    private fun parseActorNameFact(fact: String): Pair<Long, String>? {
        if (!fact.startsWith("ACTOR_NAME:")) return null
        val parts = fact.split(":", limit = 3)
        if (parts.size < 3) return null
        val actorId = parts[1].toLongOrNull() ?: return null
        return actorId to parts[2]
    }

    private fun parseActorAtFact(fact: String): Pair<Long, Boolean?>? {
        if (!fact.startsWith("ACTOR_AT:")) return null
        val parts = fact.split(":")
        if (parts.size < 4) return null

        val actorId = parts[1].toLongOrNull() ?: return null
        val imprisoned = parts
            .firstOrNull { it.startsWith("IMPRISONED=") }
            ?.substringAfter("=")
            ?.toIntOrNull()
            ?.let { it != 0 }

        return actorId to imprisoned
    }

    private suspend fun getKnownBards(
        organizerActorId: Long
    ): List<BardCandidate> {
        val knownActorIds = getKnownActorsAcrossLandsFromKnowledge(organizerActorId)
            .map { it.actorId }
            .toSet()

        if (knownActorIds.isEmpty()) return emptyList()

        return actorDao.actorsOfTypeForScenario(session.scenarioId, "BARD")
            .filter { it.actorId in knownActorIds }
            .map { actor ->
                BardCandidate(
                    actorId = actor.actorId,
                    name = actor.name,
                    lastKnownLandId = knownActorLand(actor.actorId),
                    lastSeenTurn = turns.read().turn,
                    notabilityLevel = actor.notabilityLevel
                )
            }
            .sortedBy { it.name }
    }

    private suspend fun localSilverAtLand(landId: Long): Int {
        return treasuryDao.byOwner("LAND", landId)?.silverCoins ?: 0
    }

    private fun buildBardInvitationNote(offeredPrice: Int?): String {
        val fee = offeredPrice ?: DEFAULT_BARD_FEE_SILVER
        return "Fee offered: $fee $MONEY_CODE. Please accept or decline."
    }

    private fun decideBardAcceptance(offeredPrice: Int?): Boolean {
        val fee = offeredPrice ?: 0
        return fee >= DEFAULT_BARD_FEE_SILVER
    }

    private fun computeMerchantSalary(goodsSubtotal: Int): Int {
        if (goodsSubtotal <= 0) return MIN_MERCHANT_SALARY_SILVER
        val percentFee = (goodsSubtotal * MERCHANT_SALARY_PERCENT + 99) / 100
        return max(MIN_MERCHANT_SALARY_SILVER, percentFee)
    }

    private suspend fun sendLocalMusicianMayorOrder(
        ballId: Long,
        senderActorId: Long,
        targetLandId: Long,
        venueId: Long,
        musicianCount: Int,
        eventTurn: Int,
        deliveryMethod: DeliveryMethod
    ) {
        if (musicianCount <= 0) return

        val senderLandId = actualActorLand(senderActorId) ?: session.currentLandId
        val mayorActorId = requireMayorActorIdForLand(targetLandId)

        sendTrackedMessage(
            sourceLandId = senderLandId,
            targetLandId = targetLandId,
            fromActorId = senderActorId,
            toActorId = mayorActorId,
            toRole = "MAYOR",
            messageType = MessageType.SEALED_LETTER,
            delivery = deliveryMethod,
            body = OrderComposer.compose(
                MessageDraft(
                    messageType = MessageType.SEALED_LETTER,
                    orderKind = OrderKind.HIRE_BARDS_FOR_EVENT,
                    targetActorId = mayorActorId,
                    targetLandId = targetLandId,
                    structureIds = listOf(venueId),
                    scheduledTurn = eventTurn,
                    relatedEventId = ballId,
                    freeTextNote = "Provide $musicianCount local musicians for the planned ball in venue $venueId on turn $eventTurn."
                )
            ),
            sealed = true,
            forceMovementForSameLand = deliveryMethod != DeliveryMethod.LOCAL
        )
    }

    private suspend fun sendMayorMoneyOrder(
        ballId: Long,
        senderActorId: Long,
        sourceLandId: Long,
        targetLandId: Long,
        amount: Int,
        eventTurn: Int,
        deliveryMethod: DeliveryMethod
    ) {
        if (amount <= 0) return

        val senderLandId = actualActorLand(senderActorId) ?: session.currentLandId
        val mayorActorId = requireMayorActorIdForLand(sourceLandId)

        val effectiveDelivery =
            if (deliveryMethod == DeliveryMethod.LOCAL && senderLandId != sourceLandId) {
                DeliveryMethod.MESSENGER
            } else {
                deliveryMethod
            }

        val messageType = MessageType.SEALED_LETTER

        sendTrackedMessage(
            sourceLandId = senderLandId,
            targetLandId = sourceLandId,
            fromActorId = senderActorId,
            toActorId = mayorActorId,
            toRole = "MAYOR",
            messageType = messageType,
            delivery = effectiveDelivery,
            body = OrderComposer.compose(
                MessageDraft(
                    messageType = messageType,
                    orderKind = OrderKind.TRANSPORT_MONEY,
                    targetActorId = mayorActorId,
                    lands = if (sourceLandId == targetLandId) {
                        listOf(sourceLandId)
                    } else {
                        listOf(sourceLandId, targetLandId)
                    },
                    targetLandId = targetLandId,
                    resourceAmounts = listOf(ResourceAmount(MONEY_CODE, amount)),
                    scheduledTurn = eventTurn,
                    relatedEventId = ballId,
                    allowConvoyHire = true,
                    freeTextNote = if (sourceLandId == targetLandId) {
                        "Provide local funding for the planned ball."
                    } else {
                        "Transfer money for the planned ball by convoy."
                    }
                )
            ),
            sealed = true,
            forceMovementForSameLand = effectiveDelivery != DeliveryMethod.LOCAL
        )
    }

    private suspend fun sendCoinHolderTransferOrder(
        requestorActorId: Long,
        coinHolderActorId: Long,
        targetLandId: Long,
        amountSilver: Int,
        reason: String,
        deliveryMethod: DeliveryMethod,
        scheduledTurn: Int
    ): Boolean {
        if (amountSilver <= 0) return true

        val senderLandId = actualActorLand(requestorActorId) ?: session.currentLandId
        val coinHolderLandId = resolveKnownContactLandOrInvalidate(
            viewerActorId = requestorActorId,
            senderActorId = requestorActorId,
            targetActorId = coinHolderActorId
        ) ?: return false

        val effectiveDelivery =
            if (deliveryMethod == DeliveryMethod.LOCAL && coinHolderLandId != senderLandId) {
                DeliveryMethod.MESSENGER
            } else {
                deliveryMethod
            }

        val messageType = MessageType.SEALED_LETTER

        sendTrackedMessage(
            sourceLandId = senderLandId,
            targetLandId = coinHolderLandId,
            fromActorId = requestorActorId,
            toActorId = coinHolderActorId,
            toRole = "COIN_HOLDER",
            messageType = messageType,
            delivery = effectiveDelivery,
            body = OrderComposer.compose(
                MessageDraft(
                    messageType = messageType,
                    orderKind = OrderKind.TRANSPORT_MONEY,
                    targetActorId = coinHolderActorId,
                    targetLandId = targetLandId,
                    resourceAmounts = listOf(ResourceAmount(MONEY_CODE, amountSilver)),
                    scheduledTurn = scheduledTurn,
                    freeTextNote = reason
                )
            ),
            sealed = true,
            forceMovementForSameLand = effectiveDelivery != DeliveryMethod.LOCAL
        )

        return true
    }

    private suspend fun sendPlayerMoneyTransferOrder(
        requestorActorId: Long,
        targetLandId: Long,
        amountSilver: Int,
        reason: String
    ) {
        if (amountSilver <= 0) return

        val sourceMoneyLand = getKnownMoneyStorageLand(
            viewerActorId = requestorActorId,
            targetLandId = targetLandId,
            requiredAmount = amountSilver
        ) ?: return

        sendMayorMoneyOrder(
            ballId = 0L,
            senderActorId = requestorActorId,
            sourceLandId = sourceMoneyLand,
            targetLandId = targetLandId,
            amount = amountSilver,
            eventTurn = turns.read().turn,
            deliveryMethod = DeliveryMethod.MESSENGER
        )
    }

    fun getDefaultBardFeeForNotability(notabilityLevel: Int): Int {
        return DEFAULT_BARD_FEE_SILVER + ((notabilityLevel - 1).coerceAtLeast(0) * 5)
    }

    fun previewBallBudget(draft: BallPlanDraft): BallBudgetPreview {
        val delegated = isDelegatedBallDraft(draft)
        val baseBudget = if (delegated) {
            getStandardBudgetForBallScale(draft.scale)
        } else {
            0
        }

        val bardSubtotal = if (delegated) {
            0
        } else {
            draft.bardPlans.sumOf { it.feeSilver }
        }

        return BallBudgetPreview(
            baseBudget = baseBudget,
            bardSubtotal = bardSubtotal,
            wineSubtotal = 0,
            totalBudget = baseBudget + bardSubtotal
        )
    }

    suspend fun canOrganizeBallInLand(
        landId: Long,
        orderedByActorId: Long = session.actorId
    ): Boolean {
        // Venue ownership always belongs to the requestor / host of the ball.

        return getEligibleVenueOptionsForRequestor(
            targetLandId = landId,
            requestorActorId = orderedByActorId
        ).isNotEmpty()
    }

    private suspend fun getOrganizerAndSubordinateActorIds(organizerActorId: Long): Set<Long> {
        // Keep localized and reusable.
        // Right now we safely include the organizer himself.
        // Extend this method later when subordinate hierarchy becomes explicit.
        return linkedSetOf(organizerActorId)
    }

    private suspend fun isLandOwnedByOrganizerOrSubordinates(
        landId: Long,
        organizerActorId: Long
    ): Boolean {
        val owner = OwnershipDao.ownerOf(landId) ?: return false
        val eligibleActorIds = getOrganizerAndSubordinateActorIds(organizerActorId)

        return when (owner.ownerType.uppercase()) {
            "PLAYER" -> organizerActorId == session.actorId
            "VASSAL", "NOBLE", "ACTOR", "MAYOR" -> owner.ownerRef in eligibleActorIds
            else -> false
        }
    }

    private suspend fun isStructureOwnedByOrganizerOrSubordinates(
        structureId: Long,
        organizerActorId: Long
    ): Boolean {
        val structure = structureDao.byId(structureId) ?: return false
        return isStructureOwnedByOrganizerOrSubordinates(
            structure = structure,
            organizerActorId = organizerActorId
        )
    }

    private suspend fun isStructureOwnedByOrganizerOrSubordinates(
        structure: StructureEntity,
        organizerActorId: Long
    ): Boolean {
        val eligibleActorIds = getOrganizerAndSubordinateActorIds(organizerActorId)

        return when (structure.ownerType?.trim()?.uppercase()) {
            "PLAYER" -> organizerActorId == session.actorId
            "VASSAL", "NOBLE", "ACTOR", "MAYOR" ->
                structure.ownerRef != null && structure.ownerRef in eligibleActorIds
            else -> false
        }
    }

    private suspend fun getEligibleVenueOptionsForRequestor(
        targetLandId: Long,
        requestorActorId: Long
    ): List<VenueOption> {
        return getKnownVenuesForLand(
            targetLandId = targetLandId,
            requestorActorId = requestorActorId
        )
    }

    suspend fun getCurrentChancellorActorId(
        viewerActorId: Long = session.actorId
    ): Long? = knownRoleHolderActorId(
        roleName = "CHANCELLOR",
        viewerActorId = viewerActorId
    )

    private suspend fun knownRoleHolderActorId(
        roleName: String,
        viewerActorId: Long = session.actorId
    ): Long? {
        val facts = knowledgeDao.getTrueKnowledgeForActor(viewerActorId)

        val normalizedRole = roleName.trim().uppercase()

        val patterns = listOf(
            Regex("""^ROLE_HOLDER:([A-Z_]+):ACTOR=(\d+)$"""),
            Regex("""^ACTOR_ROLE:(\d+):([A-Z_]+)$"""),
            Regex("""^ROLE:([A-Z_]+):ACTOR=(\d+)$"""),
            Regex("""^OFFICE_HOLDER:([A-Z_]+):ACTOR=(\d+)$"""),
            Regex("""^COUNCIL_ROLE:([A-Z_]+):ACTOR=(\d+)$"""),
            Regex("""^ACTOR_OFFICE:(\d+):([A-Z_]+)$""")
        )

        var bestTurn = -1
        var bestKnowledgeId = -1L
        var actorId: Long? = null

        for (k in facts) {
            val fact = k.fact.trim()

            for (regex in patterns) {
                val match = regex.matchEntire(fact) ?: continue

                val candidateRole: String
                val candidateActorId: Long

                if (regex.pattern.startsWith("^ACTOR_ROLE") || regex.pattern.startsWith("^ACTOR_OFFICE")) {
                    candidateActorId = match.groupValues[1].toLongOrNull() ?: continue
                    candidateRole = match.groupValues[2].uppercase()
                } else {
                    candidateRole = match.groupValues[1].uppercase()
                    candidateActorId = match.groupValues[2].toLongOrNull() ?: continue
                }

                if (candidateRole != normalizedRole) continue

                val better = k.turn > bestTurn || (k.turn == bestTurn && k.id > bestKnowledgeId)
                if (better) {
                    bestTurn = k.turn
                    bestKnowledgeId = k.id
                    actorId = candidateActorId
                }
            }
        }

        return actorId
    }

    private suspend fun pickMessengerForLand(landId: Long): Long {
        val available = messengerDao.byLand(landId)
        require(available.isNotEmpty()) { "No messenger available in land $landId" }

        val activeCarrierIds = movementOrderDao.getActive()
            .filter { it.carrierType == "MESSENGER" }
            .map { it.armyId }
            .toSet()

        return available.firstOrNull { it.id !in activeCarrierIds }
            ?.id
            ?: error("No idle messenger available in land $landId")
    }

    private suspend fun pickDoveForLand(landId: Long): Long {
        val available = doveDao.byHome(landId)
        require(available.isNotEmpty()) { "No dove available in land $landId" }

        val activeCarrierIds = movementOrderDao.getActive()
            .filter { it.carrierType == "DOVE" }
            .map { it.armyId }
            .toSet()

        return available.firstOrNull { it.id !in activeCarrierIds }
            ?.id
            ?: error("No idle dove available in land $landId")
    }

    suspend fun validateBallDeliveryCapacity(draft: BallPlanDraft): BallDeliveryCapacityResult {
        val delegated = draft.organizerActorId != draft.orderedByActorId

        val sourceLandId = actualActorLand(
            if (delegated) draft.orderedByActorId else draft.organizerActorId
        ) ?: draft.targetLandId

        val postOffice = postOfficeDao.get(sourceLandId)
        val messengerCapacity = postOffice?.messengerCapacity ?: 0

        val activeMovements = movementOrderDao.getActive()

        val busyMessengerIds = activeMovements
            .filter { it.carrierType == "MESSENGER" }
            .map { it.armyId }
            .toSet()

        val busyDoveIds = activeMovements
            .filter { it.carrierType == "DOVE" }
            .map { it.armyId }
            .toSet()

        val idleMessengersAtLand = messengerDao.byLand(sourceLandId)
            .filter { it.id !in busyMessengerIds }

        val idleDovesAtLand = doveDao.byHome(sourceLandId)
            .filter { it.id !in busyDoveIds }

        val availableMessengers = minOf(messengerCapacity, idleMessengersAtLand.size)
        val availableDoves = idleDovesAtLand.size

        var requiredMessengers = 0
        var requiredDoves = 0

        fun countPendingDelivery(
            deliveryMethod: DeliveryMethod,
            sendLater: Boolean,
            alreadySent: Boolean,
            alreadyDelivered: Boolean = false
        ) {
            if (sendLater || alreadySent || alreadyDelivered) return

            when (deliveryMethod) {
                DeliveryMethod.MESSENGER -> requiredMessengers++
                DeliveryMethod.DOVE -> requiredDoves++
                DeliveryMethod.LOCAL -> Unit
            }
        }

        if (delegated) {
            countPendingDelivery(
                deliveryMethod = draft.delegationOrderDeliveryMethod,
                sendLater = draft.delegationOrderSendLater,
                alreadySent = draft.delegationOrderSent,
                alreadyDelivered = draft.delegationOrderDelivered
            )

            if (draft.budgetUsesCoinHolder) {
                val budgetPreview = previewBallBudget(draft)
                if (budgetPreview.totalBudget > 0) {
                    countPendingDelivery(
                        deliveryMethod = draft.coinHolderOrderDeliveryMethod,
                        sendLater = draft.coinHolderOrderSendLater,
                        alreadySent = draft.coinHolderOrderSent,
                        alreadyDelivered = draft.coinHolderOrderDelivered
                    )
                }
            } else {
                draft.budgetFromStocks
                    .filter { it.quantity > 0 }
                    .forEach { source ->
                        countPendingDelivery(
                            deliveryMethod = source.deliveryMethod,
                            sendLater = source.sendLater,
                            alreadySent = source.orderSent,
                            alreadyDelivered = source.orderDelivered
                        )
                    }
            }
        } else {
            val localBardPlans = draft.bardPlans.filter { it.generatedLocally }
            val nonLocalBardPlans = draft.bardPlans.filterNot { it.generatedLocally }

            nonLocalBardPlans.forEach { bard ->
                countPendingDelivery(
                    deliveryMethod = bard.deliveryMethod,
                    sendLater = bard.sendLater,
                    alreadySent = bard.invitationSent
                )
            }

            val pendingLocalMusicianPlans = localBardPlans.filter {
                !it.sendLater && !it.invitationSent
            }

            if (pendingLocalMusicianPlans.isNotEmpty()) {
                countPendingDelivery(
                    deliveryMethod = pendingLocalMusicianPlans.first().deliveryMethod,
                    sendLater = false,
                    alreadySent = false
                )
            }

            draft.attendeePlans.forEach { guest ->
                countPendingDelivery(
                    deliveryMethod = guest.deliveryMethod,
                    sendLater = guest.sendLater,
                    alreadySent = guest.invitationSent
                )
            }

            if (draft.budgetUsesCoinHolder) {
                val budgetPreview = previewBallBudget(draft)
                if (budgetPreview.totalBudget > 0) {
                    countPendingDelivery(
                        deliveryMethod = draft.coinHolderOrderDeliveryMethod,
                        sendLater = draft.coinHolderOrderSendLater,
                        alreadySent = draft.coinHolderOrderSent,
                        alreadyDelivered = draft.coinHolderOrderDelivered
                    )
                }
            } else {
                draft.budgetFromStocks
                    .filter { it.quantity > 0 }
                    .forEach { source ->
                        countPendingDelivery(
                            deliveryMethod = source.deliveryMethod,
                            sendLater = source.sendLater,
                            alreadySent = source.orderSent,
                            alreadyDelivered = source.orderDelivered
                        )
                    }
            }

            draft.alcoholPlan.fromStocks
                .filter { it.code == "WINE" && it.quantity > 0 }
                .forEach { source ->
                    countPendingDelivery(
                        deliveryMethod = source.deliveryMethod,
                        sendLater = source.sendLater,
                        alreadySent = source.orderSent,
                        alreadyDelivered = source.orderDelivered
                    )
                }
        }

        val ok = requiredMessengers <= availableMessengers && requiredDoves <= availableDoves

        val problems = buildList {
            if (requiredMessengers > availableMessengers) {
                add("Only $availableMessengers messengers are available, but $requiredMessengers are required.")
            }
            if (requiredDoves > availableDoves) {
                add("Only $availableDoves doves are available, but $requiredDoves are required.")
            }
        }

        return BallDeliveryCapacityResult(
            ok = ok,
            requiredMessengers = requiredMessengers,
            availableMessengers = availableMessengers,
            requiredDoves = requiredDoves,
            availableDoves = availableDoves,
            message = problems.takeIf { it.isNotEmpty() }?.joinToString("\n")
        )
    }

    suspend fun getAvailableDeliveryCarriersForLand(landId: Long): Pair<Int, Int> {
        val postOffice = postOfficeDao.get(landId)
        val messengerCapacity = postOffice?.messengerCapacity ?: 0

        val activeMovements = movementOrderDao.getActive()

        val busyMessengerIds = activeMovements
            .filter { it.carrierType == "MESSENGER" }
            .map { it.armyId }
            .toSet()

        val busyDoveIds = activeMovements
            .filter { it.carrierType == "DOVE" }
            .map { it.armyId }
            .toSet()

        val idleMessengersAtLand = messengerDao.byLand(landId)
            .filter { it.id !in busyMessengerIds }

        val idleDovesAtLand = doveDao.byHome(landId)
            .filter { it.id !in busyDoveIds }

        val availableMessengers = minOf(messengerCapacity, idleMessengersAtLand.size)
        val availableDoves = idleDovesAtLand.size

        return availableMessengers to availableDoves
    }

    private suspend fun bookDeliveryMovementIfNeeded(
        delivery: DeliveryMethod,
        messageId: Long,
        sourceLandId: Long,
        targetLandId: Long,
        forceMovementForSameLand: Boolean = false
    ) {
        when (delivery) {
            DeliveryMethod.LOCAL -> return

            DeliveryMethod.MESSENGER -> {
                if (sourceLandId == targetLandId && !forceMovementForSameLand) return

                val messengerId = pickMessengerForLand(sourceLandId)
                movementOrderDao.upsert(
                    MovementOrderEntity(
                        id = 0,
                        armyId = messengerId,
                        carrierType = "MESSENGER",
                        messageId = messageId,
                        sourceLandId = sourceLandId,
                        targetLandId = targetLandId,
                        createdTurn = turns.read().turn,
                        status = "CREATED"
                    )
                )
            }

            DeliveryMethod.DOVE -> {
                if (sourceLandId == targetLandId && !forceMovementForSameLand) return

                val doveId = pickDoveForLand(sourceLandId)
                movementOrderDao.upsert(
                    MovementOrderEntity(
                        id = 0,
                        armyId = doveId,
                        carrierType = "DOVE",
                        messageId = messageId,
                        sourceLandId = sourceLandId,
                        targetLandId = targetLandId,
                        createdTurn = turns.read().turn,
                        status = "CREATED"
                    )
                )
            }
        }
    }

    private suspend fun sendTrackedMessage(
        sourceLandId: Long,
        targetLandId: Long,
        fromActorId: Long,
        toActorId: Long?,
        toRole: String?,
        messageType: MessageType,
        delivery: DeliveryMethod,
        body: String,
        sealed: Boolean,
        forceMovementForSameLand: Boolean = false
    ): Long {
        val actualMessageType = if (sealed || messageType == MessageType.SEALED_LETTER) {
            MessageType.SEALED_LETTER
        } else {
            messageType
        }

        val (actualDelivery, actualSealed) = requireConcreteDelivery(
            sourceLandId = sourceLandId,
            targetLandId = targetLandId,
            fromActorId = fromActorId,
            toActorId = toActorId,
            toRole = toRole,
            requested = delivery,
            sealed = actualMessageType == MessageType.SEALED_LETTER
        )

        if (actualDelivery == DeliveryMethod.LOCAL) {
            validatePersonalDeliveryLimit(fromActorId)

            if (toActorId != null) {
                val targetActuallyHere = actualActorLand(toActorId) == targetLandId
                if (!targetActuallyHere) {
                    invalidateKnownActorLocationIfObservedAbsent(
                        viewerActorId = fromActorId,
                        targetActorId = toActorId,
                        observedLandId = targetLandId
                    )
                    error("Actor $toActorId is not present in land $targetLandId. Known location was cleared.")
                }
            }
        }

        val messageId = post.send(
            from = fromActorId,
            toActorId = toActorId,
            toRole = toRole,
            messageType = actualMessageType,
            delivery = actualDelivery,
            body = body,
            turn = turns.read().turn,
            sealed = actualSealed
        )

        bookDeliveryMovementIfNeeded(
            delivery = actualDelivery,
            messageId = messageId,
            sourceLandId = sourceLandId,
            targetLandId = targetLandId,
            forceMovementForSameLand = forceMovementForSameLand || actualDelivery != DeliveryMethod.LOCAL
        )

        if (actualDelivery == DeliveryMethod.LOCAL && toActorId != null) {
            recordPersonalDeliveryConsequences(
                fromActorId = fromActorId,
                toActorId = toActorId,
                landId = sourceLandId,
                messageId = messageId
            )
        }

        return messageId
    }

    private suspend fun validatePersonalDeliveryLimit(fromActorId: Long) {
        val currentTurn = turns.read().turn
        val usedVisits = auditLogDao.countByActionAndPayloadFragment(
            turn = currentTurn,
            action = "MESSAGE_DELIVERED_PERSONALLY",
            payloadFragment = "%\"fromActorId\":\"$fromActorId\"%"
        )

        require(usedVisits < MAX_PERSONAL_DELIVERIES_PER_TURN) {
            "Personal delivery limit reached: only $MAX_PERSONAL_DELIVERIES_PER_TURN visits are allowed per turn."
        }
    }

    private suspend fun recordPersonalDeliveryConsequences(
        fromActorId: Long,
        toActorId: Long,
        landId: Long,
        messageId: Long
    ) {
        audit.log(
            "MESSAGE_DELIVERED_PERSONALLY",
            mapOf(
                "fromActorId" to fromActorId,
                "toActorId" to toActorId,
                "messageId" to messageId,
                "landId" to landId
            )
        )

        if (isProtectedOfficeVisit(fromActorId, toActorId)) return

        val visitorRank = highestKnownTitleRank(fromActorId)
        val recipientRank = highestKnownTitleRank(toActorId)

        if (visitorRank != null && recipientRank != null && recipientRank < visitorRank) {
            prestigeDao.upsert(
                PrestigeLogEntity(
                    id = 0,
                    nobleId = fromActorId,
                    delta = -1,
                    reason = "Visited a lower-title person personally to deliver a message.",
                    turn = turns.read().turn
                )
            )

            gossipDao.upsert(
                GossipEntity(
                    id = 0,
                    landId = landId,
                    text = "A higher noble personally visited a lower-title person to deliver a message.",
                    turn = turns.read().turn
                )
            )
        }
    }

    private suspend fun isProtectedOfficeVisit(
        viewerActorId: Long,
        targetActorId: Long
    ): Boolean {
        return listOf("CHANCELLOR", "COIN_HOLDER", "DEFENSE_COMMANDER").any { role ->
            knownRoleHolderActorId(
                roleName = role,
                viewerActorId = viewerActorId
            ) == targetActorId
        }
    }

    private suspend fun highestKnownTitleRank(actorId: Long): Int? {
        val ranksByTitleId = getTitleRanks()
        return nobleTitleDao.byNoble(actorId)
            .mapNotNull { ranksByTitleId[it.titleId] }
            .maxOrNull()
    }

    private fun deliveryDefaultsForLetters(): Pair<DeliveryMethod, Boolean> {
        return DeliveryMethod.MESSENGER to true
    }

    private suspend fun canDeliverByMessenger(landId: Long): Boolean {
        val postOffice = postOfficeDao.get(landId)
        val messengers = messengerDao.byLand(landId)
        return postOffice != null && postOffice.messengerCapacity > 0 && messengers.isNotEmpty()
    }

    private suspend fun canDeliverByDove(landId: Long): Boolean {
        return doveDao.byHome(landId).isNotEmpty()
    }

    private suspend fun requireConcreteDelivery(
        sourceLandId: Long,
        targetLandId: Long,
        fromActorId: Long,
        toActorId: Long?,
        toRole: String?,
        requested: DeliveryMethod,
        sealed: Boolean
    ): Pair<DeliveryMethod, Boolean> {
        return when (requested) {
            DeliveryMethod.LOCAL -> {
                require(sourceLandId == targetLandId) {
                    "Personal delivery is available only when sender and recipient are in the same land."
                }
                require(toActorId != null) {
                    "Personal delivery requires a known recipient actor."
                }
                DeliveryMethod.LOCAL to sealed
            }

            DeliveryMethod.MESSENGER -> {
                val postOffice = postOfficeDao.get(sourceLandId)
                require(postOffice != null && postOffice.messengerCapacity > 0) {
                    "No messenger/post office capacity available in land $sourceLandId"
                }
                require(messengerDao.byLand(sourceLandId).isNotEmpty()) {
                    "No messenger available in land $sourceLandId"
                }
                DeliveryMethod.MESSENGER to sealed
            }

            DeliveryMethod.DOVE -> {
                require(doveDao.byHome(sourceLandId).isNotEmpty()) {
                    "No dove available in land $sourceLandId"
                }
                DeliveryMethod.DOVE to sealed
            }
        }
    }

    private suspend fun getTitleRanks(): Map<Long, Int> {
        return titleDao.list()
            .sortedBy { it.id }
            .mapIndexed { index, title -> title.id to (index + 1) }
            .toMap()
    }

    suspend fun getLocalInviteeSuggestions(
        landId: Long,
        minimumTitleRank: Int,
        organizerActorId: Long = session.actorId
    ): List<AttendeeCandidate> {
        return getKnownInviteeNobles(organizerActorId)
            .filter { it.lastKnownLandId == landId }
            .filter { it.titleRank >= minimumTitleRank }
            .sortedBy { it.name }
    }

    suspend fun getRemoteInviteeSuggestions(
        targetLandId: Long,
        organizerActorId: Long = session.actorId
    ): List<AttendeeCandidate> {
        return getKnownInviteeNobles(organizerActorId)
            .filter { it.lastKnownLandId != null && it.lastKnownLandId != targetLandId }
            .sortedBy { it.name }
    }

    suspend fun createLocalMusicianIfNeeded(landId: Long): BardPlanItem {
        val nextId = (actorDao.actorsForScenario(session.scenarioId).maxOfOrNull { it.actorId } ?: 0L) + 1L
        val landName = LandDao.get(landId)?.name ?: "the Land"
        val baseName = localBardNames.random(Random(landId + turns.read().turn))
        val actorName = "$baseName of $landName"

        val actor = Actor(
            scenarioId = session.scenarioId,
            actorId = nextId,
            name = actorName,
            actorType = "BARD",
            notabilityLevel = 1
        )
        actorDao.upsert(actor)

        actorLocationDao.upsert(
            ActorLocation(
                scenarioId = session.scenarioId,
                actorId = nextId,
                locationId = landId
            )
        )

        publishActorKnowledgeForViewer(
            viewerActorId = session.actorId,
            actorId = nextId,
            actorName = actorName,
            landId = landId
        )

        return BardPlanItem(
            actorId = nextId,
            deliveryMethod = DeliveryMethod.MESSENGER,
            sealedLetter = true,
            oral = false,
            feeSilver = getDefaultBardFeeForNotability(1),
            notabilityLevel = 1,
            generatedLocally = true,
            sendLater = false
        )
    }

    fun getRecommendedAlcoholUnitsForBall(
        attendeeCount: Int,
        inviteLocalPeople: Boolean
    ): Int {
        val localExtra = if (inviteLocalPeople) max(4, attendeeCount / 2) else 0
        return attendeeCount + localExtra
    }

    private suspend fun findOrCreateConvoyAtLand(landId: Long): Long {
        val existing = convoyDao.inLand(landId).firstOrNull()
        if (existing != null) return existing.id

        val newConvoyId = convoyDao.upsert(
            ConvoyEntity(
                id = 0,
                armyId = null,
                landId = landId
            )
        )

        wagonDao.upsert(
            WagonEntity(
                id = 0,
                convoyId = newConvoyId,
                capacity = 100
            )
        )

        return newConvoyId
    }

    private suspend fun sendWineTransportOrder(
        ballId: Long,
        senderActorId: Long,
        sourceLandId: Long,
        targetLandId: Long,
        amount: Int,
        eventTurn: Int,
        deliveryMethod: DeliveryMethod
    ) {
        if (amount <= 0) return

        val senderLandId = actualActorLand(senderActorId) ?: session.currentLandId
        val mayorActorId = requireMayorActorIdForLand(sourceLandId)

        val effectiveDelivery =
            if (deliveryMethod == DeliveryMethod.LOCAL && senderLandId != sourceLandId) {
                DeliveryMethod.MESSENGER
            } else {
                deliveryMethod
            }

        val messageType = MessageType.SEALED_LETTER

        sendTrackedMessage(
            sourceLandId = senderLandId,
            targetLandId = sourceLandId,
            fromActorId = senderActorId,
            toActorId = mayorActorId,
            toRole = "MAYOR",
            messageType = messageType,
            delivery = effectiveDelivery,
            body = OrderComposer.compose(
                MessageDraft(
                    messageType = messageType,
                    orderKind = OrderKind.TRANSPORT_RESOURCES,
                    targetActorId = mayorActorId,
                    lands = if (sourceLandId == targetLandId) {
                        listOf(sourceLandId)
                    } else {
                        listOf(sourceLandId, targetLandId)
                    },
                    targetLandId = targetLandId,
                    resourceAmounts = listOf(ResourceAmount("WINE", amount)),
                    scheduledTurn = eventTurn,
                    relatedEventId = ballId,
                    allowConvoyHire = sourceLandId != targetLandId,
                    freeTextNote = if (sourceLandId == targetLandId) {
                        "Reserve $amount wine from local stocks for the planned ball."
                    } else {
                        "Move wine for the planned ball."
                    }
                )
            ),
            sealed = true,
            forceMovementForSameLand = effectiveDelivery != DeliveryMethod.LOCAL
        )
    }

    private suspend fun actualActorLand(actorId: Long): Long? {
        if (session.scenarioId <= 0L || actorId <= 0L) return null
        return actorLocationDao.getLocation(
            scenarioId = session.scenarioId,
            actorId = actorId
        )?.locationId
    }

    private suspend fun markKnownActorLocationUnknown(
        viewerActorId: Long,
        targetActorId: Long
    ) {
        if (viewerActorId <= 0L || targetActorId <= 0L) return

        knowledgeDao.upsert(
            KnowledgeEntryEntity(
                id = 0,
                actorId = viewerActorId,
                fact = "ACTOR_LOCATION_UNKNOWN:$targetActorId",
                sourceMsgId = 0,
                turn = turns.read().turn,
                confirmed = true
            )
        )
    }

    private suspend fun publishActorKnowledgeForViewer(
        viewerActorId: Long,
        actorId: Long,
        actorName: String,
        landId: Long,
        imprisoned: Boolean = false
    ) {
        if (viewerActorId <= 0L || actorId <= 0L) return

        val turn = turns.read().turn

        knowledgeDao.upsert(
            KnowledgeEntryEntity(
                id = 0,
                actorId = viewerActorId,
                fact = "ACTOR_NAME:$actorId:$actorName",
                sourceMsgId = 0,
                turn = turn,
                confirmed = true
            )
        )

        knowledgeDao.upsert(
            KnowledgeEntryEntity(
                id = 0,
                actorId = viewerActorId,
                fact = "ACTOR_AT:$actorId:LAND=$landId:IMPRISONED=${if (imprisoned) 1 else 0}",
                sourceMsgId = 0,
                turn = turn,
                confirmed = true
            )
        )
    }

    private suspend fun resolveKnownContactLandOrInvalidate(
        viewerActorId: Long,
        senderActorId: Long,
        targetActorId: Long
    ): Long? {
        return knownActorLand(
            viewerActorId = viewerActorId,
            actorId = targetActorId
        )
    }

    suspend fun getBallPlanDraft(ballId: Long): BallPlanDraft? {
        val ball = plannedBallDao.byId(ballId) ?: return null
        val bards = plannedBallBardDao.forBall(ballId)
        val guests = plannedBallGuestDao.forBall(ballId)
        val alcohol = plannedBallAlcoholDao.forBall(ballId)

        return BallPlanDraft(
            targetLandId = ball.landId,
            orderedByActorId = ball.orderedByActorId,
            organizerActorId = ball.organizerActorId,
            eventTurn = ball.eventTurn.takeIf { it >= 0 },
            bardPlans = bards.map {
                BardPlanItem(
                    actorId = it.bardActorId,
                    deliveryMethod = it.deliveryMethod,
                    sealedLetter = it.messageType == MessageType.SEALED_LETTER,
                    oral = it.messageType == MessageType.ORAL,
                    feeSilver = it.feeSilver,
                    notabilityLevel = it.notabilityLevel,
                    generatedLocally = it.generatedLocally,
                    sendLater = it.sendLater,
                    invitationSent = it.invitationSent
                )
            },
            attendeePlans = guests.map {
                GuestPlanItem(
                    actorId = it.actorId,
                    deliveryMethod = it.deliveryMethod,
                    sealedLetter = it.messageType == MessageType.SEALED_LETTER,
                    oral = it.messageType == MessageType.ORAL,
                    sendLater = it.sendLater,
                    invitationSent = it.invitationSent
                )
            },
            localMusicianCount = ball.localMusicianCount,
            inviteLocalPeople = ball.inviteLocalPeople,
            venueStructureId = ball.venueStructureId,
            localInviteMinimumTitleRank = ball.localInviteMinimumTitleRank,
            alcoholPlan = BallAlcoholPlan(
                fromStocks = alcohol
                    .filter { it.sourceType == "STOCK" }
                    .map {
                        StockUse(
                            code = it.code,
                            quantity = it.quantity,
                            landId = it.sourceLandId ?: ball.landId,
                            deliveryMethod = it.transportDeliveryMethod ?: DeliveryMethod.MESSENGER,
                            sendLater = it.transportSendLater,
                            orderSent = it.transportOrderSent,
                            orderDelivered = it.transportOrderDelivered
                        )
                    },
                fromMarket = alcohol
                    .filter { it.sourceType == "MARKET" }
                    .map {
                        MarketBuy(
                            marketLandId = it.sourceLandId ?: ball.landId,
                            code = it.code,
                            quantity = it.quantity,
                            unitPrice = it.unitPrice,
                            transportDelivery = it.transportDeliveryMethod ?: DeliveryMethod.MESSENGER,
                            transportSealed = it.transportSealedLetter
                        )
                    }
            ),
            budgetFromStocks = alcohol
                .filter { it.sourceType == "BUDGET" && it.code == MONEY_CODE }
                .map {
                    StockUse(
                        code = MONEY_CODE,
                        quantity = it.quantity,
                        landId = it.sourceLandId ?: ball.landId,
                        deliveryMethod = it.transportDeliveryMethod ?: DeliveryMethod.MESSENGER,
                        sendLater = it.transportSendLater,
                        orderSent = it.transportOrderSent,
                        orderDelivered = it.transportOrderDelivered
                    )
                },
            coinHolderOrderDeliveryMethod = ball.coinHolderOrderDeliveryMethod,
            coinHolderOrderSendLater = ball.coinHolderOrderSendLater,
            delegationOrderDeliveryMethod = ball.delegationOrderDeliveryMethod,
            delegationOrderSendLater = ball.delegationOrderSendLater,
            scale = ball.scale,
            useAlcohol = ball.useAlcohol,
            budgetUsesCoinHolder = ball.budgetUsesCoinHolder,
            coinHolderOrderSent = ball.coinHolderOrderSent,
            coinHolderOrderDelivered = ball.coinHolderOrderDelivered,
            delegationOrderSent = ball.delegationOrderSent,
            delegationOrderDelivered = ball.delegationOrderDelivered
        )
    }

    suspend fun markBallUsesLocalMusician(ballId: Long) {
        val ball = plannedBallDao.byId(ballId) ?: return

        plannedBallDao.upsert(
            ball.copy(
                localMusicianCount = max(1, ball.localMusicianCount)
            )
        )

        updateBallPlanReadiness(ballId)
    }

    suspend fun setBallLocalMusicianCount(
        ballId: Long,
        count: Int
    ) {
        val ball = plannedBallDao.byId(ballId) ?: return

        plannedBallDao.upsert(
            ball.copy(
                localMusicianCount = count.coerceAtLeast(0)
            )
        )

        updateBallPlanReadiness(ballId)
    }

    private suspend fun createBallPlanDraft(draft: BallPlanDraft): Long {
        val delegated = isDelegatedBallDraft(draft)
        val normalizedScale = normalizeBallScale(draft.scale) ?: BALL_SCALE_MIDDLE

        val ballId = plannedBallDao.upsert(
            PlannedBallEntity(
                orderedByActorId = draft.orderedByActorId,
                organizerActorId = draft.organizerActorId,
                landId = draft.targetLandId,
                eventTurn = draft.eventTurn ?: turns.read().turn,
                venueStructureId = draft.venueStructureId,
                status = "PLANNING",
                inviteLocalPeople = draft.inviteLocalPeople,
                localInviteMinimumTitleRank = draft.localInviteMinimumTitleRank,
                localMusicianCount = if (delegated) 0 else draft.localMusicianCount,
                scale = normalizedScale,
                useAlcohol = if (delegated) draft.useAlcohol else false,
                budgetUsesCoinHolder = draft.budgetUsesCoinHolder,
                coinHolderOrderDeliveryMethod = draft.coinHolderOrderDeliveryMethod,
                coinHolderOrderSendLater = draft.coinHolderOrderSendLater,
                delegationOrderDeliveryMethod = draft.delegationOrderDeliveryMethod,
                delegationOrderSendLater = draft.delegationOrderSendLater
            )
        )

        draft.bardPlans.forEach {
            plannedBallBardDao.upsert(
                PlannedBallBardEntity(
                    plannedBallId = ballId,
                    bardActorId = it.actorId,
                    invitationSent = false,
                    accepted = null,
                    deliveryMethod = if (delegated) DeliveryMethod.MESSENGER else it.deliveryMethod,
                    messageType = if (delegated) {
                        MessageType.SEALED_LETTER
                    } else {
                        resolveInvitationMessageType(
                            oral = it.oral,
                            sealedLetter = it.sealedLetter
                        )
                    },
                    feeSilver = if (delegated) 0 else it.feeSilver,
                    notabilityLevel = it.notabilityLevel,
                    generatedLocally = if (delegated) false else it.generatedLocally,
                    sendLater = if (delegated) false else it.sendLater
                )
            )
        }

        draft.attendeePlans.forEach {
            plannedBallGuestDao.upsert(
                PlannedBallGuestEntity(
                    plannedBallId = ballId,
                    actorId = it.actorId,
                    invitationSent = false,
                    accepted = null,
                    deliveryMethod = if (delegated) DeliveryMethod.MESSENGER else it.deliveryMethod,
                    messageType = if (delegated) {
                        MessageType.SEALED_LETTER
                    } else {
                        resolveInvitationMessageType(
                            oral = it.oral,
                            sealedLetter = it.sealedLetter
                        )
                    },
                    sendLater = if (delegated) false else it.sendLater
                )
            )
        }

        if (!delegated) {
            draft.alcoholPlan.fromStocks.forEach {
                plannedBallAlcoholDao.upsert(
                    PlannedBallAlcoholEntity(
                        plannedBallId = ballId,
                        code = it.code,
                        quantity = it.quantity,
                        sourceLandId = it.landId,
                        sourceType = "STOCK",
                        transportDeliveryMethod = it.deliveryMethod,
                        transportSealedLetter = it.deliveryMethod != DeliveryMethod.LOCAL,
                        transportSendLater = it.sendLater
                    )
                )
            }

            draft.alcoholPlan.fromMarket.forEach {
                plannedBallAlcoholDao.upsert(
                    PlannedBallAlcoholEntity(
                        plannedBallId = ballId,
                        code = it.code,
                        quantity = it.quantity,
                        sourceLandId = it.marketLandId,
                        sourceType = "MARKET",
                        unitPrice = it.unitPrice,
                        transportDeliveryMethod = it.transportDelivery,
                        transportSealedLetter = it.transportSealed,
                        transportSendLater = false
                    )
                )
            }

            draft.budgetFromStocks.forEach {
                plannedBallAlcoholDao.upsert(
                    PlannedBallAlcoholEntity(
                        plannedBallId = ballId,
                        code = MONEY_CODE,
                        quantity = it.quantity,
                        sourceLandId = it.landId,
                        sourceType = "BUDGET",
                        transportDeliveryMethod = it.deliveryMethod,
                        transportSealedLetter = it.deliveryMethod != DeliveryMethod.LOCAL,
                        transportSendLater = it.sendLater
                    )
                )
            }
        }

        updateBallPlanReadiness(ballId)
        return ballId
    }

    suspend fun getActiveBallPlanForLand(landId: Long): BallPreparationState? {
        val currentActorId = session.actorId

        val candidates = plannedBallDao
            .activeForLandCandidates(landId)
            .filter { ball ->
                ball.orderedByActorId == currentActorId || ball.organizerActorId == currentActorId
            }

        if (candidates.isEmpty()) return null

        val active = candidates.maxWithOrNull(
            compareBy<PlannedBallEntity>(
                { if (it.orderedByActorId == currentActorId) 1 else 0 },
                { if (it.organizerActorId == currentActorId) 1 else 0 },
                { it.id }
            )
        ) ?: return null

        return getBallPreparationState(active.id)
    }

    suspend fun saveBallPlanDraft(
        draft: BallPlanDraft,
        existingBallId: Long? = null
    ): BallPreparationState {
        val ballId = if (existingBallId != null) {
            overwriteExistingBallPlan(existingBallId, draft)
        } else {
            createBallPlanDraft(draft)
        }

        updateBallPlanReadiness(ballId)
        return getBallPreparationState(ballId)
            ?: error("Could not load saved ball preparation state for ballId=$ballId")
    }

    suspend fun cancelBallPlan(ballId: Long) {
        val ball = plannedBallDao.byId(ballId) ?: return
        plannedBallDao.upsert(
            ball.copy(status = "CANCELLED")
        )
    }

    private suspend fun decideBardAcceptanceForBallTurn(
        bardActorId: Long,
        eventTurn: Int,
        offeredPrice: Int?
    ): Boolean {
        val acceptedByFee = decideBardAcceptance(offeredPrice)
        if (!acceptedByFee) return false

        val activeBalls = ballDao.forTurn(eventTurn)
        val bardAlreadyBusy = activeBalls.any { ballEvent ->
            val performers = plannedBallBardDao.forBall(ballEvent.plannedBallId)
            performers.any { it.bardActorId == bardActorId && it.accepted != false }
        }

        return !bardAlreadyBusy
    }

    suspend fun finalizeBallPlan(
        ballId: Long,
        deliveryForRemoteContacts: DeliveryMethod? = null
    ): BallFinalizeResult {
        val ball = plannedBallDao.byId(ballId)
            ?: return BallFinalizeResult.Blocked("Ball plan was not found.")

        val state = getBallPreparationState(ballId)
            ?: return BallFinalizeResult.Blocked("Ball preparation state was not found.")

        if (!state.ready) {
            return BallFinalizeResult.Blocked(
                "Ball plan is not ready: ${state.missingItems.joinToString("; ")}"
            )
        }

        val draft = getBallPlanDraft(ballId)
            ?: return BallFinalizeResult.Blocked("Ball draft was not found.")

        val delegated = isDelegatedPlannedBall(ball)
        val eventTurn = ball.eventTurn
        val venueId = ball.venueStructureId
            ?: return BallFinalizeResult.Blocked("Ball plan has no venue.")

        val bards = plannedBallBardDao.forBall(ballId)
        val guests = plannedBallGuestDao.forBall(ballId)

        val localMusicianPlans = bards.filter { it.generatedLocally }
        val validBards = bards.filter { !it.generatedLocally && it.bardActorId > 0L }
        val validGuests = guests.filter { it.actorId > 0L }

        val directBudgetSources = draft.budgetFromStocks.filter { it.quantity > 0 && !it.sendLater }
        val usesCoinHolderForBudget = draft.budgetUsesCoinHolder
        val budgetPreview = previewBallBudget(draft)

        if (!delegated) {
            val organizerLandId = actualActorLand(ball.organizerActorId) ?: session.currentLandId

            validBards.forEach { bard ->
                if (bard.sendLater) return@forEach

                val targetLand = resolveKnownContactLandOrInvalidate(
                    viewerActorId = ball.organizerActorId,
                    senderActorId = ball.organizerActorId,
                    targetActorId = bard.bardActorId
                ) ?: return@forEach

                val requestedType = when (bard.messageType) {
                    MessageType.ORAL -> MessageType.ORAL
                    MessageType.SEALED_LETTER -> MessageType.SEALED_LETTER
                    else -> MessageType.LETTER
                }

                val msgType = normalizeInvitationMessageTypeForLocal(
                    sourceLandId = organizerLandId,
                    targetLandId = targetLand,
                    requestedType = requestedType
                )

                val note = buildBardInvitationNote(bard.feeSilver)

                sendTrackedMessage(
                    sourceLandId = organizerLandId,
                    targetLandId = targetLand,
                    fromActorId = ball.organizerActorId,
                    toActorId = bard.bardActorId,
                    toRole = null,
                    messageType = msgType,
                    delivery = bard.deliveryMethod,
                    body = OrderComposer.compose(
                        buildBallInvitationDraft(
                            messageType = msgType,
                            inviteeActorId = bard.bardActorId,
                            targetLandId = ball.landId,
                            venueId = venueId,
                            eventTurn = eventTurn,
                            note = note
                        )
                    ),
                    sealed = isMessageSealed(msgType)
                )

                val accepted = decideBardAcceptanceForBallTurn(
                    bardActorId = bard.bardActorId,
                    eventTurn = eventTurn,
                    offeredPrice = bard.feeSilver
                )

                plannedBallBardDao.upsert(
                    bard.copy(
                        invitationSent = true,
                        accepted = accepted,
                        deliveryMethod = bard.deliveryMethod,
                        messageType = msgType
                    )
                )
            }

            val immediateLocalMusicianPlans = localMusicianPlans.filter { !it.sendLater }
            if (immediateLocalMusicianPlans.isNotEmpty()) {
                val localDeliveryMethod = immediateLocalMusicianPlans.first().deliveryMethod
                val musicianCount = maxOf(
                    immediateLocalMusicianPlans.size,
                    ball.localMusicianCount.coerceAtLeast(0)
                )

                sendLocalMusicianMayorOrder(
                    ballId = ballId,
                    senderActorId = ball.organizerActorId,
                    targetLandId = ball.landId,
                    venueId = venueId,
                    musicianCount = musicianCount,
                    eventTurn = eventTurn,
                    deliveryMethod = localDeliveryMethod
                )

                localMusicianPlans.forEach { localPlan ->
                    if (localPlan.sendLater) return@forEach
                    plannedBallBardDao.upsert(
                        localPlan.copy(
                            invitationSent = true,
                            accepted = null,
                            deliveryMethod = localDeliveryMethod,
                            messageType = MessageType.SEALED_LETTER
                        )
                    )
                }
            }

            validGuests.forEach { guest ->
                if (guest.sendLater) return@forEach

                val targetLand = resolveKnownContactLandOrInvalidate(
                    viewerActorId = ball.organizerActorId,
                    senderActorId = ball.organizerActorId,
                    targetActorId = guest.actorId
                ) ?: return@forEach

                val requestedType = when (guest.messageType) {
                    MessageType.ORAL -> MessageType.ORAL
                    MessageType.SEALED_LETTER -> MessageType.SEALED_LETTER
                    else -> MessageType.LETTER
                }
                val msgType = normalizeInvitationMessageTypeForLocal(
                    sourceLandId = organizerLandId,
                    targetLandId = targetLand,
                    requestedType = requestedType
                )

                sendTrackedMessage(
                    sourceLandId = organizerLandId,
                    targetLandId = targetLand,
                    fromActorId = ball.organizerActorId,
                    toActorId = guest.actorId,
                    toRole = null,
                    messageType = msgType,
                    delivery = guest.deliveryMethod,
                    body = OrderComposer.compose(
                        buildBallInvitationDraft(
                            messageType = msgType,
                            inviteeActorId = guest.actorId,
                            targetLandId = ball.landId,
                            venueId = venueId,
                            eventTurn = eventTurn,
                            note = null
                        )
                    ),
                    sealed = isMessageSealed(msgType)
                )

                plannedBallGuestDao.upsert(
                    guest.copy(
                        invitationSent = true,
                        accepted = null
                    )
                )
            }

            if (usesCoinHolderForBudget && budgetPreview.totalBudget > 0) {
                val coinHolderActorId = knownRoleHolderActorId(
                    roleName = "COIN_HOLDER",
                    viewerActorId = ball.orderedByActorId
                ) ?: return BallFinalizeResult.Blocked(
                    "No coin holder is known to the requestor."
                )

                val coinHolderLandId = knownActorLand(
                    viewerActorId = draft.orderedByActorId,
                    actorId = coinHolderActorId
                ) ?: return BallFinalizeResult.Blocked(
                    "The location of the coin holder is not known, so the order cannot be delivered."
                )

                val sent = sendCoinHolderTransferOrder(
                    requestorActorId = ball.organizerActorId,
                    coinHolderActorId = coinHolderActorId,
                    targetLandId = ball.landId,
                    amountSilver = budgetPreview.totalBudget,
                    reason = "Provide budget for organizing the delegated ball.",
                    deliveryMethod = draft.coinHolderOrderDeliveryMethod,
                    scheduledTurn = eventTurn
                )
                if (!sent) {
                    return BallFinalizeResult.Blocked(
                        "The location of the coin holder is not known, so the order cannot be delivered."
                    )
                }
            } else {
                draft.budgetFromStocks
                    .filter { it.quantity > 0 && it.landId != ball.landId && !it.sendLater }
                    .forEach { row ->
                        sendMayorMoneyOrder(
                            ballId = ballId,
                            senderActorId = ball.organizerActorId,
                            sourceLandId = row.landId,
                            targetLandId = ball.landId,
                            amount = row.quantity,
                            eventTurn = eventTurn,
                            deliveryMethod = row.deliveryMethod
                        )
                    }
            }

            draft.alcoholPlan.fromStocks
                .filter { it.code == "WINE" && it.quantity > 0 && !it.sendLater }
                .forEach { row ->
                    sendWineTransportOrder(
                        ballId = ballId,
                        senderActorId = ball.organizerActorId,
                        sourceLandId = row.landId,
                        targetLandId = ball.landId,
                        amount = row.quantity,
                        eventTurn = eventTurn,
                        deliveryMethod = row.deliveryMethod ?: DeliveryMethod.MESSENGER
                    )
                }

            if (draft.alcoholPlan.fromMarket.any { it.quantity > 0 }) {
                requestMerchantToBuyAlcohol(ballId, delivery = DeliveryMethod.LOCAL)
            }

            val ballEventId = ballDao.upsert(
                BallEventEntity(
                    id = 0,
                    landId = ball.landId,
                    turn = eventTurn,
                    orderedByActorId = ball.orderedByActorId,
                    organizerActorId = ball.organizerActorId,
                    venueStructureId = venueId,
                    plannedBallId = ballId,
                    scale = ball.scale,
                    useAlcohol = ball.useAlcohol
                )
            )

            plannedBallDao.upsert(
                ball.copy(
                    status = "FINALIZED",
                    finalizedBallEventId = ballEventId
                )
            )

            return BallFinalizeResult.Success("The ball was released.")
        }

        val requestorLandId = actualActorLand(ball.orderedByActorId) ?: session.currentLandId
        val chancellorKnownLand = resolveKnownContactLandOrInvalidate(
            viewerActorId = ball.orderedByActorId,
            senderActorId = ball.orderedByActorId,
            targetActorId = ball.organizerActorId
        ) ?: run {
            plannedBallDao.upsert(
                ball.copy(status = "PLANNING")
            )
            updateBallPlanReadiness(ballId)
            return BallFinalizeResult.Blocked(
                "The location of the chancellor is not known, so the order cannot be delivered."
            )
        }

        if (draft.delegationOrderSendLater) {
            return BallFinalizeResult.Blocked(
                "The order to the chancellor is marked send later."
            )
        }

        val chancellorDelivery =
            if (draft.delegationOrderDeliveryMethod == DeliveryMethod.LOCAL && requestorLandId != chancellorKnownLand) {
                DeliveryMethod.MESSENGER
            } else {
                draft.delegationOrderDeliveryMethod
            }

        val chancellorMessageType =
            if (chancellorDelivery == DeliveryMethod.LOCAL) MessageType.ORAL else MessageType.SEALED_LETTER

        val bardHintIds = validBards.map { it.bardActorId }.distinct()
        val guestHintIds = validGuests.map { it.actorId }.distinct()
        val allHintIds = (bardHintIds + guestHintIds).distinct()

        if (usesCoinHolderForBudget && budgetPreview.totalBudget > 0) {
            val coinHolderActorId = knownRoleHolderActorId(
                roleName = "COIN_HOLDER",
                viewerActorId = ball.organizerActorId
            ) ?: return BallFinalizeResult.Blocked(
                "No coin holder is known to the organizer."
            )

            val coinHolderLandId = knownActorLand(
                viewerActorId = draft.orderedByActorId,
                actorId = coinHolderActorId
            ) ?: return BallFinalizeResult.Blocked(
                "The location of the coin holder is not known, so the order cannot be delivered."
            )

            val sent = sendCoinHolderTransferOrder(
                requestorActorId = ball.organizerActorId,
                coinHolderActorId = coinHolderActorId,
                targetLandId = chancellorKnownLand,
                amountSilver = budgetPreview.totalBudget,
                reason = "Provide budget for organizing the delegated ball.",
                scheduledTurn = eventTurn,
                deliveryMethod = draft.coinHolderOrderDeliveryMethod
            )
            if (!sent) {
                return BallFinalizeResult.Blocked(
                    "The location of the coin holder is not known, so the order cannot be delivered."
                )
            }
        } else {
            directBudgetSources.forEach { row ->
                sendMayorMoneyOrder(
                    ballId = ballId,
                    senderActorId = ball.orderedByActorId,
                    sourceLandId = row.landId,
                    targetLandId = chancellorKnownLand,
                    amount = row.quantity,
                    eventTurn = eventTurn,
                    deliveryMethod = row.deliveryMethod
                )
            }
        }

        val delegatedNote = buildString {
            append("Organize a ball on behalf of actor ${ball.orderedByActorId}. ")
            append("Venue structure id: $venueId. ")
            append("Event land: ${ball.landId}. ")
            append("Planned turn: $eventTurn. ")
            append("Scale: ${normalizeBallScale(ball.scale) ?: "UNSPECIFIED"}. ")
            append("Minimum invitee title rank: ${ball.localInviteMinimumTitleRank}. ")

            if (ball.useAlcohol) {
                append("Alcohol may be used if appropriate for the requestor. ")
            } else {
                append("Do not plan alcohol unless later ordered otherwise. ")
            }

            if (bardHintIds.isNotEmpty()) {
                append("Desired bard hints: ${bardHintIds.joinToString(", ")}. ")
            } else {
                append("No bard hints were provided. ")
            }

            if (guestHintIds.isNotEmpty()) {
                append("Desired additional invitee hints: ${guestHintIds.joinToString(", ")}. ")
            } else {
                append("No additional invitee hints were provided. ")
            }

            if (usesCoinHolderForBudget && budgetPreview.totalBudget > 0) {
                append("A separate order was sent to the coin holder to provide the ball budget to your location. ")
            } else if (directBudgetSources.isNotEmpty()) {
                append("Money transfer orders were sent directly to known mayors to provide the delegated ball budget to your location. ")
            } else {
                append("No budget transfer was required. ")
            }

            append("The bard and invitee lists above are only hints; final selection is at your discretion.")
        }

        sendTrackedMessage(
            sourceLandId = requestorLandId,
            targetLandId = chancellorKnownLand,
            fromActorId = ball.orderedByActorId,
            toActorId = ball.organizerActorId,
            toRole = "CHANCELLOR",
            messageType = chancellorMessageType,
            delivery = chancellorDelivery,
            body = OrderComposer.compose(
                MessageDraft(
                    messageType = chancellorMessageType,
                    orderKind = OrderKind.ORGANIZE_BALL,
                    actorIds = allHintIds,
                    targetActorId = ball.organizerActorId,
                    targetLandId = ball.landId,
                    structureIds = listOf(venueId),
                    scheduledTurn = eventTurn,
                    relatedEventId = ballId,
                    freeTextNote = delegatedNote
                )
            ),
            sealed = isMessageSealed(chancellorMessageType),
            forceMovementForSameLand = chancellorDelivery != DeliveryMethod.LOCAL
        )

        plannedBallDao.upsert(
            ball.copy(
                status = "ORDERED_TO_CHANCELLOR",
                finalizedBallEventId = null,
                delegationOrderSent = true,
                delegationOrderDelivered = false
            )
        )

        return BallFinalizeResult.Success("The order was sent to the chancellor.")
    }

    suspend fun getCurrentCoinHolderActorId(
        viewerActorId: Long = session.actorId
    ): Long? = knownRoleHolderActorId(
        roleName = "COIN_HOLDER",
        viewerActorId = viewerActorId
    )

    suspend fun getKnownActorLandForViewer(
        viewerActorId: Long,
        actorId: Long
    ): Long? = knownActorLand(
        viewerActorId = viewerActorId,
        actorId = actorId
    )

    private suspend fun resolveBallMessageSourceLand(
        organizerActorId: Long,
        fallbackLandId: Long
    ): Long {
        val actual = actualActorLand(organizerActorId)
        return actual ?: fallbackLandId
    }

    private suspend fun validatePersonalBallDelivery(
        senderActorId: Long,
        recipientActorId: Long,
        sourceLandId: Long,
        recipientKnownLandId: Long?
    ) {
        val senderLand = actualActorLand(senderActorId)
            ?: throw IllegalStateException("Actor $senderActorId has no known current land for personal delivery.")

        if (senderLand != sourceLandId) {
            throw IllegalStateException(
                "Actor $senderActorId is not present in land $sourceLandId for personal delivery."
            )
        }

        val recipientLand = recipientKnownLandId ?: actualActorLand(recipientActorId)
        if (recipientLand != sourceLandId) {
            throw IllegalStateException(
                "Actor $recipientActorId is not present in land $sourceLandId for personal delivery."
            )
        }
    }



    suspend fun requestMerchantToBuyAlcohol(
        ballId: Long,
        delivery: DeliveryMethod = DeliveryMethod.LOCAL
    ) {
        require(delivery == DeliveryMethod.LOCAL) {
            "Merchant funding must be initiated locally."
        }

        val ball = plannedBallDao.byId(ballId) ?: return
        val draft = getBallPlanDraft(ballId)
        val alcohol = plannedBallAlcoholDao.forBall(ballId)
            .filter { it.code == "WINE" }

        val marketWine = alcohol.filter { it.sourceType == "MARKET" }
        val stockWineElsewhere = alcohol.filter {
            it.sourceType == "STOCK" &&
                    it.sourceLandId != null &&
                    it.sourceLandId != ball.landId
        }

        if (marketWine.isEmpty() && stockWineElsewhere.isEmpty()) return

        val organizerActorId = ball.organizerActorId
        val organizerLandId = actualActorLand(organizerActorId) ?: session.currentLandId
        val coinHolderActorId = knownRoleHolderActorId("COIN_HOLDER")

        val goodsSubtotal = marketWine.sumOf { it.quantity * it.unitPrice }
        if (goodsSubtotal > 0) {
            val merchantSalary = computeMerchantSalary(goodsSubtotal)
            val finalMoneyRequired = goodsSubtotal + merchantSalary

            val fundingSenderActorId = coinHolderActorId ?: organizerActorId
            val moneyKnowledgeActorId = coinHolderActorId ?: organizerActorId

            val sourceMoneyLand = getKnownMoneyStorageLand(
                viewerActorId = moneyKnowledgeActorId,
                targetLandId = ball.landId,
                requiredAmount = finalMoneyRequired
            )

            if (sourceMoneyLand != null) {
                sendMayorMoneyOrder(
                    ballId = ballId,
                    senderActorId = fundingSenderActorId,
                    sourceLandId = sourceMoneyLand,
                    targetLandId = ball.landId,
                    amount = finalMoneyRequired,
                    eventTurn = ball.eventTurn,
                    deliveryMethod = draft?.coinHolderOrderDeliveryMethod ?: DeliveryMethod.MESSENGER
                )
            }
        }

        marketWine.groupBy { requireNotNull(it.sourceLandId) }.forEach { (marketLandId, lines) ->
            val wineQty = lines.sumOf { it.quantity }
            val purchaseDelivery =
                if (organizerLandId == marketLandId) DeliveryMethod.LOCAL else DeliveryMethod.MESSENGER
            val purchaseMessageType =
                if (purchaseDelivery == DeliveryMethod.LOCAL) MessageType.ORAL else MessageType.LETTER

            sendTrackedMessage(
                sourceLandId = organizerLandId,
                targetLandId = marketLandId,
                fromActorId = organizerActorId,
                toActorId = null,
                toRole = "MERCHANT",
                messageType = purchaseMessageType,
                delivery = purchaseDelivery,
                body = OrderComposer.compose(
                    MessageDraft(
                        messageType = purchaseMessageType,
                        orderKind = OrderKind.BUY_GOODS,
                        lands = listOf(marketLandId, ball.landId),
                        targetLandId = ball.landId,
                        resourceAmounts = listOf(ResourceAmount("WINE", wineQty)),
                        scheduledTurn = ball.eventTurn,
                        relatedEventId = ballId,
                        freeTextNote = "Buy wine for the planned ball."
                    )
                ),
                sealed = purchaseDelivery != DeliveryMethod.LOCAL
            )

            if (marketLandId != ball.landId) {
                sendWineTransportOrder(
                    ballId = ballId,
                    senderActorId = organizerActorId,
                    sourceLandId = marketLandId,
                    targetLandId = ball.landId,
                    amount = wineQty,
                    eventTurn = ball.eventTurn,
                    deliveryMethod = delivery ?: DeliveryMethod.MESSENGER
                )
            }
        }

        stockWineElsewhere.groupBy { requireNotNull(it.sourceLandId) }.forEach { (sourceLandId, lines) ->
            val wineQty = lines.sumOf { it.quantity }

            sendWineTransportOrder(
                ballId = ballId,
                senderActorId = organizerActorId,
                sourceLandId = sourceLandId,
                targetLandId = ball.landId,
                amount = wineQty,
                eventTurn = ball.eventTurn,
                deliveryMethod = delivery ?: DeliveryMethod.MESSENGER
            )
        }
    }

    private fun shouldReplaceLatestKnowledgeFact(
        current: KnowledgeEntryEntity?,
        candidate: KnowledgeEntryEntity
    ): Boolean {
        if (current == null) return true
        if (candidate.turn != current.turn) return candidate.turn > current.turn
        return candidate.id > current.id
    }

    private fun parseStructureOwnerKnowledgeFact(
        fact: String
    ): Triple<Long, String, Long?>? {
        val patterns = listOf(
            Regex("""^STRUCTURE_OWNER:(\d+):TYPE=([A-Z_]+)(?::REF=(\d+))?$"""),
            Regex("""^STRUCTURE_OWNER:(\d+):OWNER_TYPE=([A-Z_]+)(?::OWNER_REF=(\d+))?$"""),
            Regex("""^STRUCTURE_OWNED_BY:(\d+):TYPE=([A-Z_]+)(?::REF=(\d+))?$"""),
            Regex("""^OWNER_OF_STRUCTURE:(\d+):TYPE=([A-Z_]+)(?::REF=(\d+))?$""")
        )

        for (regex in patterns) {
            val match = regex.matchEntire(fact.trim()) ?: continue
            val structureId = match.groupValues[1].toLong()
            val ownerType = match.groupValues[2]
            val ownerRef = match.groupValues.getOrNull(3)
                ?.takeIf { it.isNotBlank() }
                ?.toLongOrNull()

            return Triple(structureId, ownerType, ownerRef)
        }

        return null
    }

    private fun parseStructureStatusKnowledgeFact(
        fact: String
    ): Pair<Long, String>? {
        val trimmed = fact.trim()

        Regex("""^STRUCTURE_STATUS:(\d+):STATUS=([A-Z_]+)$""")
            .matchEntire(trimmed)
            ?.let { match ->
                return match.groupValues[1].toLong() to match.groupValues[2]
            }

        Regex("""^STRUCTURE_STATE:(\d+):STATE=([A-Z_]+)$""")
            .matchEntire(trimmed)
            ?.let { match ->
                return match.groupValues[1].toLong() to match.groupValues[2]
            }

        Regex("""^STRUCTURE_DESTROYED:(\d+)$""")
            .matchEntire(trimmed)
            ?.let { match ->
                return match.groupValues[1].toLong() to "DESTROYED"
            }

        return null
    }

    private fun isStructureUnavailableByLatestKnownStatus(
        status: String?
    ): Boolean {
        return when (status?.trim()?.uppercase()) {
            "DESTROYED", "RUINED", "DEMOLISHED", "COLLAPSED", "BURNED_DOWN" -> true
            else -> false
        }
    }

    private suspend fun getBallPreparationState(
        ballId: Long
    ): BallPreparationState? {
        val ball = plannedBallDao.byId(ballId) ?: return null
        val bards = plannedBallBardDao.forBall(ballId)
        val guests = plannedBallGuestDao.forBall(ballId)
        val alcohol = plannedBallAlcoholDao.forBall(ballId)

        val draft = getBallPlanDraft(ballId)

        val venueOptions = getEligibleVenueOptionsForRequestor(
            targetLandId = ball.landId,
            requestorActorId = ball.orderedByActorId
        )

        val hasVenue = ball.venueStructureId != null
        val venueOwnershipOk = ball.venueStructureId != null &&
                venueOptions.any { it.structureId == ball.venueStructureId }

        val organizerPresent = ball.organizerActorId > 0L
        val delegated = isDelegatedPlannedBall(ball)
        val hasScale = !normalizeBallScale(ball.scale).isNullOrBlank()
        val hasMinimumTitle = ball.localInviteMinimumTitleRank >= 0

        val realBardPlans = bards.filter { !it.generatedLocally && it.bardActorId > 0L }
        val realGuestPlans = guests.filter { it.actorId > 0L }

        val acceptedBardCount = realBardPlans.count { it.invitationSent && it.accepted == true }
        val acceptedGuestCount = realGuestPlans.count { it.invitationSent && it.accepted == true }

        val localMusiciansSelected =
            ball.localMusicianCount > 0 ||
                    (draft?.localMusicianCount ?: 0) > 0 ||
                    bards.any { it.generatedLocally }

        val hasBards = if (delegated) {
            realBardPlans.isNotEmpty() || localMusiciansSelected
        } else {
            acceptedBardCount > 0 || localMusiciansSelected
        }

        val guestsPrepared = if (delegated) {
            true
        } else {
            acceptedGuestCount > 0
        }

        val hasAlcohol = !ball.useAlcohol ||
                alcohol.any { it.code == "WINE" && it.quantity > 0 }

        val delegationRequested = organizerPresent && ball.organizerActorId != ball.orderedByActorId

        val currentChancellorActorId = if (delegationRequested) {
            getCurrentChancellorActorId(viewerActorId = ball.orderedByActorId)
        } else {
            null
        }

        val delegationValid = !delegationRequested || (
                currentChancellorActorId != null &&
                        currentChancellorActorId == ball.organizerActorId
                )

        val knownOrganizerLand = if (delegationRequested) {
            knownActorLand(
                viewerActorId = ball.orderedByActorId,
                actorId = ball.organizerActorId
            )
        } else {
            null
        }

        val deliveryCapacity = draft?.let { validateBallDeliveryCapacity(it) }
        val deliveryCapacityOk = deliveryCapacity?.ok ?: true
        val deliveryMessage = deliveryCapacity?.message

        val usesCoinHolderForBudget = draft?.budgetUsesCoinHolder ?: ball.budgetUsesCoinHolder
        val budgetPreview = draft?.let { previewBallBudget(it) }
        val requiredBudget = budgetPreview?.totalBudget ?: 0

        val localBudgetAvailable = requiredBudget <= 0 ||
                localSilverAtLand(ball.landId) >= requiredBudget

        val selectedBudgetAvailable = draft?.let {
            it.budgetFromStocks
                .filter { row -> row.quantity > 0 && !row.sendLater }
                .sumOf { row -> row.quantity } + localSilverAtLand(ball.landId) >= requiredBudget
        } ?: localBudgetAvailable

        val budgetAvailable = if (delegated) {
            true
        } else if (usesCoinHolderForBudget) {
            localBudgetAvailable
        } else {
            selectedBudgetAvailable
        }

        val baseMissingItems = mutableListOf<String>()

        if (ball.eventTurn <= turns.read().turn) {
            baseMissingItems += "Choose a future turn for the ball."
        }
        if (!hasVenue) {
            baseMissingItems += "Choose a venue."
        }
        if (hasVenue && !venueOwnershipOk) {
            baseMissingItems += "Choose a manor or palace owned by the person who ordered the ball."
        }
        if (!organizerPresent) {
            baseMissingItems += "Choose an organizer."
        }
        if (!hasMinimumTitle) {
            baseMissingItems += "Choose the minimum title of invitees."
        }

        val delegationMissingItems = baseMissingItems.toMutableList()

        if (!hasScale) {
            delegationMissingItems += "Choose the scale of the ball."
        }
        if (!delegationValid) {
            delegationMissingItems += if (currentChancellorActorId == null) {
                "No chancellor is appointed."
            } else {
                "The selected organizer is not the appointed chancellor."
            }
        }
        if (delegationRequested && knownOrganizerLand == null) {
            delegationMissingItems += "The chancellor's location is unknown."
        }
        if (ball.delegationOrderSendLater) {
            delegationMissingItems += "The order to the chancellor is marked send later."
        }
        if (!deliveryCapacityOk) {
            delegationMissingItems += deliveryMessage ?: "Delivery capacity is insufficient."
        }

        val hasDeferredDelegatedBudgetTransfers = draft
            ?.budgetFromStocks
            ?.any { it.quantity > 0 && it.sendLater }
            ?: false

        if (hasDeferredDelegatedBudgetTransfers) {
            delegationMissingItems += "Some selected delegated budget transfers are marked send later."
        }

        val releaseMissingItems = baseMissingItems.toMutableList()

        if (!hasBards) {
            releaseMissingItems += "At least one bard must agree, or local musicians must be selected."
        }
        if (acceptedGuestCount <= 0) {
            releaseMissingItems += "At least one invitee must agree."
        }
        if (!budgetAvailable) {
            releaseMissingItems += "The required budget is not available in the event land or selected immediate transfers."
        }
        if (!deliveryCapacityOk) {
            releaseMissingItems += deliveryMessage ?: "Delivery capacity is insufficient."
        }

        val hasDeferredBudgetTransfers = draft
            ?.budgetFromStocks
            ?.any { it.quantity > 0 && it.landId != ball.landId && it.sendLater }
            ?: false

        if (hasDeferredBudgetTransfers) {
            releaseMissingItems += "Some selected budget transfers are marked send later."
        }

        val hasDeferredWineTransfers = draft
            ?.alcoholPlan
            ?.fromStocks
            ?.any { it.code == "WINE" && it.quantity > 0 && it.sendLater }
            ?: false

        if (hasDeferredWineTransfers) {
            releaseMissingItems += "Some selected wine transfers are marked send later."
        }

        val readyToSendDelegationOrder = delegationMissingItems.isEmpty()
        val readyToRelease = releaseMissingItems.isEmpty()

        val activeMissingItems = if (delegated) {
            delegationMissingItems
        } else {
            releaseMissingItems
        }

        return BallPreparationState(
            ballId = ball.id,
            landId = ball.landId,
            eventTurn = ball.eventTurn,
            venueStructureId = ball.venueStructureId,
            hasVenue = hasVenue,
            venueOwnershipOk = venueOwnershipOk,
            hasBards = hasBards,
            hasAlcohol = hasAlcohol,
            guestsPrepared = guestsPrepared,
            deliveryCapacityOk = deliveryCapacityOk,
            organizerPresent = organizerPresent,
            missingItems = activeMissingItems,
            ready = if (delegated) readyToSendDelegationOrder else readyToRelease,
            acceptedBardCount = acceptedBardCount,
            acceptedGuestCount = acceptedGuestCount,
            plannedBardCount = realBardPlans.size + maxOf(ball.localMusicianCount, draft?.localMusicianCount ?: 0),
            plannedGuestCount = realGuestPlans.size,
            readyToSendDelegationOrder = readyToSendDelegationOrder,
            readyToRelease = readyToRelease,
            budgetAvailable = budgetAvailable,
            delegated = delegated
        )
    }

    suspend fun sendBallMessagesNow(ballId: Long): BallMessageSendResult {
        val ball = plannedBallDao.byId(ballId)
            ?: return BallMessageSendResult.Blocked("The ball draft was not found.")

        val draft = getBallPlanDraft(ballId)
            ?: return BallMessageSendResult.Blocked("The ball draft was not found.")

        val venueId = draft.venueStructureId ?: ball.venueStructureId
        ?: return BallMessageSendResult.Blocked("The venue is not selected.")

        val eventTurn = draft.eventTurn ?: ball.eventTurn
        if (eventTurn <= turns.read().turn) {
            return BallMessageSendResult.Blocked("Choose a future turn for the ball.")
        }

        val delegatedToChancellor = ball.organizerActorId != ball.orderedByActorId

        val organizerActorId = ball.organizerActorId
        val organizerLandId = actualActorLand(organizerActorId)
            ?: return BallMessageSendResult.Blocked("The organizer current location is unknown.")

        val sentMessages = mutableListOf<String>()
        val skipped = mutableListOf<String>()

        suspend fun trySend(
            label: String,
            onSuccess: suspend () -> Unit = {},
            block: suspend () -> Unit
        ) {
            try {
                block()
                onSuccess()
                sentMessages += label
            } catch (e: IllegalStateException) {
                skipped += "$label: location is no longer valid. Known location was cleared."
            }
        }

        val persistedBards = plannedBallBardDao.forBall(ballId)
        val persistedGuests = plannedBallGuestDao.forBall(ballId)
        val persistedAlcohol = plannedBallAlcoholDao.forBall(ballId)

        if (!delegatedToChancellor) {
            val localMusicianPlans = persistedBards.filter { it.generatedLocally }
            val localMusicianCount = maxOf(ball.localMusicianCount, localMusicianPlans.size)

            if (localMusicianCount > 0) {
                val immediateLocalPlans = localMusicianPlans.filter { !it.sendLater }
                val alreadySent = immediateLocalPlans.any { it.invitationSent }

                if (alreadySent) {
                    skipped += "Order to mayor: provide local musicians was already sent."
                } else if (immediateLocalPlans.isNotEmpty()) {
                    val deliveryMethod = immediateLocalPlans.first().deliveryMethod
                    if (deliveryMethod == DeliveryMethod.LOCAL) {
                        skipped += "Order to mayor: provide local musicians requires personal delivery."
                    } else {
                        val targetLandName = LandDao.get(ball.landId)?.name ?: "the ball land"

                        trySend(
                            label = "Order to mayor: provide $localMusicianCount local musicians in $targetLandName",
                            onSuccess = {
                                immediateLocalPlans.forEach { plan ->
                                    plannedBallBardDao.upsert(
                                        plan.copy(
                                            invitationSent = true,
                                            accepted = null,
                                            messageType = MessageType.SEALED_LETTER
                                        )
                                    )
                                }
                            }
                        ) {
                            sendLocalMusicianMayorOrder(
                                ballId = ballId,
                                senderActorId = organizerActorId,
                                targetLandId = ball.landId,
                                venueId = venueId,
                                musicianCount = localMusicianCount,
                                eventTurn = eventTurn,
                                deliveryMethod = deliveryMethod
                            )
                        }
                    }
                }
            }

            persistedBards
                .filter { !it.generatedLocally }
                .filter { !it.sendLater }
                .filter { it.deliveryMethod != DeliveryMethod.LOCAL }
                .forEach { bard ->
                    val bardName = actorDao.byId(session.scenarioId, bard.bardActorId)?.name ?: "Unknown bard"

                    if (bard.invitationSent) {
                        skipped += "Invitation to bard $bardName was already sent."
                        return@forEach
                    }

                    val targetLandId = knownActorLand(
                        viewerActorId = organizerActorId,
                        actorId = bard.bardActorId
                    )

                    if (targetLandId == null) {
                        skipped += "$bardName: location is unknown."
                        return@forEach
                    }

                    trySend(
                        label = "Invitation to bard $bardName",
                        onSuccess = {
                            plannedBallBardDao.upsert(
                                bard.copy(
                                    invitationSent = true,
                                    accepted = decideBardAcceptanceForBallTurn(
                                        bardActorId = bard.bardActorId,
                                        eventTurn = eventTurn,
                                        offeredPrice = bard.feeSilver
                                    ),
                                    messageType = MessageType.SEALED_LETTER
                                )
                            )
                        }
                    ) {
                        sendTrackedMessage(
                            sourceLandId = organizerLandId,
                            targetLandId = targetLandId,
                            fromActorId = organizerActorId,
                            toActorId = bard.bardActorId,
                            toRole = null,
                            messageType = MessageType.SEALED_LETTER,
                            delivery = bard.deliveryMethod,
                            body = OrderComposer.compose(
                                MessageDraft(
                                    messageType = MessageType.SEALED_LETTER,
                                    orderKind = OrderKind.INVITE_TO_BALL,
                                    actorIds = listOf(bard.bardActorId),
                                    targetLandId = ball.landId,
                                    structureIds = listOf(venueId),
                                    scheduledTurn = eventTurn,
                                    relatedEventId = ballId,
                                    freeTextNote = buildBardInvitationNote(bard.feeSilver)
                                )
                            ),
                            sealed = true,
                            forceMovementForSameLand = true
                        )
                    }
                }

            persistedGuests
                .filter { !it.sendLater }
                .filter { it.deliveryMethod != DeliveryMethod.LOCAL }
                .forEach { guest ->
                    val guestName = actorDao.byId(session.scenarioId, guest.actorId)?.name ?: "Unknown noble"

                    if (guest.invitationSent) {
                        skipped += "Invitation to noble $guestName was already sent."
                        return@forEach
                    }

                    val targetLandId = knownActorLand(
                        viewerActorId = organizerActorId,
                        actorId = guest.actorId
                    )

                    if (targetLandId == null) {
                        skipped += "$guestName: location is unknown."
                        return@forEach
                    }

                    trySend(
                        label = "Invitation to noble $guestName",
                        onSuccess = {
                            plannedBallGuestDao.upsert(
                                guest.copy(
                                    invitationSent = true,
                                    messageType = MessageType.SEALED_LETTER
                                )
                            )
                        }
                    ) {
                        sendTrackedMessage(
                            sourceLandId = organizerLandId,
                            targetLandId = targetLandId,
                            fromActorId = organizerActorId,
                            toActorId = guest.actorId,
                            toRole = null,
                            messageType = MessageType.SEALED_LETTER,
                            delivery = guest.deliveryMethod,
                            body = OrderComposer.compose(
                                MessageDraft(
                                    messageType = MessageType.SEALED_LETTER,
                                    orderKind = OrderKind.INVITE_TO_BALL,
                                    actorIds = listOf(guest.actorId),
                                    targetLandId = ball.landId,
                                    structureIds = listOf(venueId),
                                    scheduledTurn = eventTurn,
                                    relatedEventId = ballId
                                )
                            ),
                            sealed = true,
                            forceMovementForSameLand = true
                        )
                    }
                }

            persistedAlcohol
                .filter { it.sourceType == "STOCK" && it.code == "WINE" }
                .filter { it.quantity > 0 }
                .filter { !it.transportSendLater }
                .filter { it.transportDeliveryMethod != DeliveryMethod.LOCAL }
                .forEach { source ->
                    val sourceLandId = source.sourceLandId
                        ?: run {
                            skipped += "Wine source: unknown land."
                            return@forEach
                        }

                    val sourceLandName = LandDao.get(sourceLandId)?.name ?: "unknown land"

                    if (source.transportOrderSent) {
                        skipped += "Order to mayor: wine transfer from $sourceLandName was already sent."
                        return@forEach
                    }

                    trySend(
                        label = "Order to mayor: transfer ${source.quantity} wine from $sourceLandName",
                        onSuccess = {
                            plannedBallAlcoholDao.upsert(
                                source.copy(transportOrderSent = true)
                            )
                        }
                    ) {
                        sendWineTransportOrder(
                            ballId = ballId,
                            senderActorId = organizerActorId,
                            sourceLandId = sourceLandId,
                            targetLandId = ball.landId,
                            amount = source.quantity,
                            eventTurn = eventTurn,
                            deliveryMethod = source.transportDeliveryMethod ?: DeliveryMethod.MESSENGER
                        )
                    }
                }
        }

        persistedAlcohol
            .filter { it.sourceType == "BUDGET" }
            .filter { it.quantity > 0 }
            .filter { !it.transportSendLater }
            .filter { it.transportDeliveryMethod != DeliveryMethod.LOCAL }
            .forEach { source ->
                val sourceLandId = source.sourceLandId
                    ?: run {
                        skipped += "Money source: unknown land."
                        return@forEach
                    }

                val sourceLandName = LandDao.get(sourceLandId)?.name ?: "unknown land"

                if (source.transportOrderSent) {
                    skipped += "Order to mayor: money transfer from $sourceLandName was already sent."
                    return@forEach
                }

                trySend(
                    label = "Order to mayor: transfer ${source.quantity} silver from $sourceLandName",
                    onSuccess = {
                        plannedBallAlcoholDao.upsert(
                            source.copy(transportOrderSent = true)
                        )
                    }
                ) {
                    sendMayorMoneyOrder(
                        ballId = ballId,
                        senderActorId = ball.orderedByActorId,
                        sourceLandId = sourceLandId,
                        targetLandId = ball.landId,
                        amount = source.quantity,
                        eventTurn = eventTurn,
                        deliveryMethod = source.transportDeliveryMethod ?: DeliveryMethod.MESSENGER
                    )
                }
            }

        if (
            ball.budgetUsesCoinHolder &&
            !ball.coinHolderOrderSendLater &&
            ball.coinHolderOrderDeliveryMethod != DeliveryMethod.LOCAL
        ) {
            if (ball.coinHolderOrderSent) {
                skipped += "Order to coin holder was already sent."
            } else {
                val coinHolderActorId = knownRoleHolderActorId(
                    roleName = "COIN_HOLDER",
                    viewerActorId = ball.orderedByActorId
                )

                if (coinHolderActorId == null) {
                    skipped += "Coin holder: no coin holder is known to the requestor."
                } else {
                    val coinHolderName = actorDao.byId(session.scenarioId, coinHolderActorId)?.name ?: "Coin holder"
                    val coinHolderLandId = knownActorLand(
                        viewerActorId = ball.orderedByActorId,
                        actorId = coinHolderActorId
                    )

                    if (coinHolderLandId == null) {
                        skipped += "Order to coin holder $coinHolderName: location is unknown."
                    } else {
                        trySend(
                            label = "Order to coin holder $coinHolderName: provide ball budget",
                            onSuccess = {
                                val latestBall = plannedBallDao.byId(ballId) ?: ball
                                plannedBallDao.upsert(
                                    latestBall.copy(coinHolderOrderSent = true)
                                )
                            }
                        ) {
                            sendCoinHolderTransferOrder(
                                requestorActorId = ball.orderedByActorId,
                                coinHolderActorId = coinHolderActorId,
                                targetLandId = ball.landId,
                                amountSilver = previewBallBudget(draft).totalBudget,
                                reason = "Provide budget for planned ball #$ballId.",
                                scheduledTurn = eventTurn,
                                deliveryMethod = ball.coinHolderOrderDeliveryMethod
                            )
                        }
                    }
                }
            }
        }

        if (
            delegatedToChancellor &&
            !ball.delegationOrderSendLater &&
            ball.delegationOrderDeliveryMethod != DeliveryMethod.LOCAL
        ) {
            if (ball.delegationOrderSent) {
                skipped += "Order to chancellor was already sent."
            } else {
                val chancellorName = actorDao.byId(session.scenarioId, ball.organizerActorId)?.name ?: "Chancellor"
                val chancellorLandId = knownActorLand(
                    viewerActorId = ball.orderedByActorId,
                    actorId = ball.organizerActorId
                )

                if (chancellorLandId == null) {
                    skipped += "Order to chancellor $chancellorName: location is unknown."
                } else {
                    val requestorLandId = actualActorLand(ball.orderedByActorId)
                        ?: return BallMessageSendResult.Blocked("The requestor current location is unknown.")

                    trySend(
                        label = "Order to chancellor $chancellorName: organize ball",
                        onSuccess = {
                            val latestBall = plannedBallDao.byId(ballId) ?: ball
                            plannedBallDao.upsert(
                                latestBall.copy(delegationOrderSent = true)
                            )
                        }
                    ) {
                        sendTrackedMessage(
                            sourceLandId = requestorLandId,
                            targetLandId = chancellorLandId,
                            fromActorId = ball.orderedByActorId,
                            toActorId = ball.organizerActorId,
                            toRole = "CHANCELLOR",
                            messageType = MessageType.SEALED_LETTER,
                            delivery = ball.delegationOrderDeliveryMethod,
                            body = OrderComposer.compose(
                                MessageDraft(
                                    messageType = MessageType.SEALED_LETTER,
                                    orderKind = OrderKind.ORGANIZE_BALL,
                                    targetActorId = ball.organizerActorId,
                                    targetLandId = ball.landId,
                                    structureIds = listOf(venueId),
                                    scheduledTurn = eventTurn,
                                    relatedEventId = ballId,
                                    freeTextNote = "Organize a ${(ball.scale ?: "middle").lowercase()} ball."
                                )
                            ),
                            sealed = true,
                            forceMovementForSameLand = true
                        )
                    }
                }
            }
        }

        updateBallPlanReadiness(ballId)

        return if (sentMessages.isEmpty() && skipped.isNotEmpty()) {
            BallMessageSendResult.Blocked(skipped.joinToString("\n"))
        } else {
            BallMessageSendResult.Success(
                sentCount = sentMessages.size,
                sentMessages = sentMessages,
                skippedMessages = skipped
            )
        }
    }

    suspend fun updateBallPlanReadiness(ballId: Long) {
        val ball = plannedBallDao.byId(ballId) ?: return
        val state = getBallPreparationState(ballId) ?: return

        val newStatus = when {
            ball.status == "CANCELLED" -> ball.status
            ball.status == "FINALIZED" -> ball.status
            ball.status == "ORDERED_TO_CHANCELLOR" -> ball.status
            state.ready -> "READY"
            else -> "PLANNING"
        }

        plannedBallDao.upsert(
            ball.copy(status = newStatus)
        )
    }

    suspend fun canUseLocalMusician(landId: Long): Boolean {
        return true
    }

    private fun buildBallInvitationDraft(
        messageType: MessageType,
        inviteeActorId: Long,
        targetLandId: Long,
        venueId: Long,
        eventTurn: Int,
        note: String?
    ): MessageDraft {
        require(inviteeActorId > 0L) {
            "Ball invitation requires a real actor id, got $inviteeActorId"
        }

        return MessageDraft(
            messageType = messageType,
            orderKind = OrderKind.INVITE_TO_BALL,
            actorIds = listOf(inviteeActorId),
            targetActorId = inviteeActorId,
            targetLandId = targetLandId,
            structureIds = listOf(venueId),
            scheduledTurn = eventTurn,
            freeTextNote = note
        )
    }

    suspend fun getPlannedBallsForLand(landId: Long): List<PlannedBallEntity> {
        return plannedBallDao
            .activeForOrganizer(session.actorId)
            .filter { it.landId == landId }
    }

    private suspend fun getKnownVenuesForLand(
        targetLandId: Long,
        requestorActorId: Long = session.actorId
    ): List<VenueOption> {
        val facts = knowledgeDao.getTrueKnowledgeForActor(requestorActorId)
        val eligibleActorIds = getOrganizerAndSubordinateActorIds(requestorActorId)

        val structureRegex =
            Regex("""^STRUCTURE:(\d+):LAND=(\d+):TYPE=([A-Z_]+):NAME=(.+)$""")

        val latestStructureFacts = mutableMapOf<Long, KnowledgeEntryEntity>()
        val latestOwnerFacts = mutableMapOf<Long, KnowledgeEntryEntity>()
        val latestStatusFacts = mutableMapOf<Long, KnowledgeEntryEntity>()

        for (knowledgeEntry in facts) {
            val fact = knowledgeEntry.fact.trim()

            val structureMatch = structureRegex.matchEntire(fact)
            if (structureMatch != null) {
                val structureId = structureMatch.groupValues[1].toLong()
                val current = latestStructureFacts[structureId]
                if (shouldReplaceLatestKnowledgeFact(current, knowledgeEntry)) {
                    latestStructureFacts[structureId] = knowledgeEntry
                }
                continue
            }

            val ownerFact = parseStructureOwnerKnowledgeFact(fact)
            if (ownerFact != null) {
                val structureId = ownerFact.first
                val current = latestOwnerFacts[structureId]
                if (shouldReplaceLatestKnowledgeFact(current, knowledgeEntry)) {
                    latestOwnerFacts[structureId] = knowledgeEntry
                }
                continue
            }

            val statusFact = parseStructureStatusKnowledgeFact(fact)
            if (statusFact != null) {
                val structureId = statusFact.first
                val current = latestStatusFacts[structureId]
                if (shouldReplaceLatestKnowledgeFact(current, knowledgeEntry)) {
                    latestStatusFacts[structureId] = knowledgeEntry
                }
            }
        }

        val result = mutableListOf<VenueOption>()

        for ((structureId, structureFactEntry) in latestStructureFacts) {
            val structureMatch = structureRegex.matchEntire(structureFactEntry.fact.trim()) ?: continue

            val landId = structureMatch.groupValues[2].toLong()
            val type = structureMatch.groupValues[3]
            val name = structureMatch.groupValues[4]

            if (landId != targetLandId) continue
            if (type != "MANOR" && type != "PALACE") continue

            val latestStatus = latestStatusFacts[structureId]
                ?.fact
                ?.trim()
                ?.let { parseStructureStatusKnowledgeFact(it)?.second }

            if (isStructureUnavailableByLatestKnownStatus(latestStatus)) {
                continue
            }

            val ownershipFact = latestOwnerFacts[structureId]
                ?.fact
                ?.trim()
                ?.let { parseStructureOwnerKnowledgeFact(it) }

            val ownershipMatches = when (ownershipFact?.second?.uppercase()) {
                "PLAYER" -> requestorActorId == session.actorId
                "VASSAL", "NOBLE", "ACTOR", "MAYOR" ->
                    ownershipFact.third != null && ownershipFact.third in eligibleActorIds
                else -> false
            }

            if (!ownershipMatches) continue

            result += VenueOption(
                structureId = structureId,
                landId = landId,
                name = name,
                type = type
            )
        }

        return result
            .distinctBy { it.structureId }
            .sortedBy { it.name }
    }

    private suspend fun getKnownBudgetStocks(
        organizerActorId: Long,
        targetLandId: Long
    ): List<AlcoholStockOption> {
        val facts = knowledgeDao.getTrueKnowledgeForActor(organizerActorId)
        val patterns = listOf(
            Regex("""^LAND_TREASURY:(\d+):SILVER=(\d+)$"""),
            Regex("""^LAND_COINS:(\d+):SILVER=(\d+)$"""),
            Regex("""^LAND_SILVER:(\d+):QTY=(\d+)$"""),
            Regex("""^LAND_RESOURCE:(\d+):ITEM=SILVER:QTY=(\d+)$""")
        )

        val bestKnownByLand = linkedMapOf<Long, Pair<Int, Int>>()

        facts.forEach { entry ->
            val fact = entry.fact.trim()
            patterns.forEach { pattern ->
                val match = pattern.matchEntire(fact) ?: return@forEach
                val landId = match.groupValues[1].toLongOrNull() ?: return@forEach
                val qty = match.groupValues[2].toIntOrNull() ?: return@forEach

                val old = bestKnownByLand[landId]
                if (old == null || entry.turn >= old.second) {
                    bestKnownByLand[landId] = qty to entry.turn
                }
            }
        }

        val actualLocalSilver = localSilverAtLand(targetLandId)
        if (actualLocalSilver > 0) {
            val old = bestKnownByLand[targetLandId]
            if (old == null || actualLocalSilver > old.first) {
                bestKnownByLand[targetLandId] = actualLocalSilver to turns.read().turn
            }
        }

        return bestKnownByLand.entries
            .mapNotNull { (landId, pair) ->
                val qty = pair.first
                if (qty <= 0) {
                    null
                } else {
                    AlcoholStockOption(
                        code = MONEY_CODE,
                        availableQty = qty,
                        landId = landId
                    )
                }
            }
            .sortedBy { it.landId }
    }

    private suspend fun getKnownAlcoholStocks(
        knowerActorId: Long = session.actorId
    ): List<AlcoholStockOption> {
        val facts = knowledgeDao.getTrueKnowledgeForActor(knowerActorId)

        val resourceRegex =
            Regex("""^LAND_RESOURCE:(\d+):ITEM=([A-Z_]+):QTY=(\d+)$""")

        val alcoholCodes = setOf("WINE", "BEER", "MEAD", "ALE")
        val result = mutableListOf<AlcoholStockOption>()

        for (k in facts) {
            val m = resourceRegex.matchEntire(k.fact.trim()) ?: continue
            val landId = m.groupValues[1].toLong()
            val code = m.groupValues[2]
            val qty = m.groupValues[3].toInt()

            if (code in alcoholCodes && qty > 0) {
                result += com.example.mygame.engine_and_helpers.entertainment_and_social.AlcoholStockOption(
                    code = code,
                    availableQty = qty,
                    landId = landId
                )
            }
        }

        return result.sortedWith(compareBy({ it.code }, { it.landId }))
    }

    private suspend fun getKnownAlcoholMarkets(
        targetLandId: Long,
        knowerActorId: Long = session.actorId
    ): List<MarketAlcoholOption> {
        val facts = knowledgeDao.getTrueKnowledgeForActor(knowerActorId)

        val marketRegex =
            Regex("""^MARKET_RESOURCE:(\d+):ITEM=([A-Z_]+):QTY=(\d+):PRICE=(\d+)$""")

        val alcoholCodes = setOf("WINE", "BEER", "MEAD", "ALE")
        val result = mutableListOf<MarketAlcoholOption>()

        for (k in facts) {
            val m = marketRegex.matchEntire(k.fact.trim()) ?: continue
            val landId = m.groupValues[1].toLong()
            val code = m.groupValues[2]
            val qty = m.groupValues[3].toInt()
            val price = m.groupValues[4].toInt()

            if (code in alcoholCodes && qty > 0) {
                result += MarketAlcoholOption(
                    landId = landId,
                    code = code,
                    availableQty = qty,
                    unitPrice = price
                )
            }
        }

        return result
            .sortedWith(compareBy({ it.landId }, { it.code }, { it.unitPrice }))
    }

    suspend fun previewBallPlan(draft: BallPlanDraft): BallPlanPreview {
        val delegated = isDelegatedBallDraft(draft)

        val venueOptions = getEligibleVenueOptionsForRequestor(
            targetLandId = draft.targetLandId,
            requestorActorId = draft.orderedByActorId
        )

        val hasVenue = draft.venueStructureId != null &&
                venueOptions.any { it.structureId == draft.venueStructureId }

        val hasScale = !normalizeBallScale(draft.scale).isNullOrBlank()
        val hasMinimumTitle = draft.localInviteMinimumTitleRank >= 0
        val hasPerformers = draft.bardPlans.isNotEmpty() || draft.localMusicianCount > 0
        val hasInvitees = draft.attendeePlans.isNotEmpty()

        val deliveryCheck = if (delegated) {
            BallDeliveryCapacityResult(
                ok = true,
                requiredMessengers = 0,
                availableMessengers = 0,
                requiredDoves = 0,
                availableDoves = 0,
                message = null
            )
        } else {
            validateBallDeliveryCapacity(draft)
        }

        val missing = buildList {
            if (draft.eventTurn == null) add("Choose the event turn.")
            if (!hasVenue) add("Choose a manor or palace owned by the person who ordered the ball.")

            if (delegated) {
                if (!hasMinimumTitle) add("Choose the minimum title of invitees.")
                if (!hasScale) add("Choose the scale of the ball.")
            } else {
                if (!hasPerformers) add("At least one bard or local musician is required.")
                if (!hasInvitees) add("At least one invitee is required.")
                if (!deliveryCheck.ok) add(deliveryCheck.message ?: "Invitation delivery capacity is insufficient.")
            }
        }

        return BallPlanPreview(
            targetLandId = draft.targetLandId,
            eventTurn = draft.eventTurn ?: turns.read().turn,
            bardActorIds = draft.bardPlans.map { it.actorId },
            attendeeActorIds = draft.attendeePlans.map { it.actorId },
            inviteLocalPeople = draft.inviteLocalPeople,
            venueStructureId = draft.venueStructureId ?: 0L,
            alcoholPlan = draft.alcoholPlan,
            missing = missing
        )
    }

    suspend fun executeManualBallPlan(
        draft: BallPlanDraft,
        deliveryForRemoteContacts: DeliveryMethod,
        generalInvitationText: String? = null
    ): List<Long> {
        val preview = previewBallPlan(draft)
        require(preview.isReady) { "Ball plan is incomplete: ${preview.missing.joinToString()}" }

        val sentIds = mutableListOf<Long>()
        val eventTurn = draft.eventTurn!!
        val venueId = draft.venueStructureId!!
        val organizerLandId = actualActorLand(draft.organizerActorId) ?: session.currentLandId

        draft.bardPlans.forEach { bardPlan ->
            val targetLand = knownActorLand(
                viewerActorId = draft.organizerActorId,
                actorId = bardPlan.actorId
            ) ?: draft.targetLandId

            val requestedType = resolveInvitationMessageType(
                oral = bardPlan.oral,
                sealedLetter = bardPlan.sealedLetter
            )
            val msgType = normalizeInvitationMessageTypeForLocal(
                sourceLandId = organizerLandId,
                targetLandId = targetLand,
                requestedType = requestedType
            )

            sentIds += sendTrackedMessage(
                sourceLandId = organizerLandId,
                targetLandId = targetLand,
                fromActorId = draft.organizerActorId,
                toActorId = bardPlan.actorId,
                toRole = null,
                messageType = msgType,
                delivery = bardPlan.deliveryMethod,
                body = OrderComposer.compose(
                    MessageDraft(
                        messageType = msgType,
                        orderKind = OrderKind.INVITE_TO_BALL,
                        actorIds = listOf(bardPlan.actorId),
                        targetLandId = draft.targetLandId,
                        structureIds = listOf(venueId),
                        scheduledTurn = eventTurn,
                        freeTextNote = generalInvitationText ?: "Fee offered: ${bardPlan.feeSilver} $MONEY_CODE."
                    )
                ),
                sealed = isMessageSealed(msgType)
            )
        }

        draft.attendeePlans.forEach { guestPlan ->
            val targetLand = knownActorLand(
                viewerActorId = draft.organizerActorId,
                actorId = guestPlan.actorId
            ) ?: draft.targetLandId

            val requestedType = resolveInvitationMessageType(
                oral = guestPlan.oral,
                sealedLetter = guestPlan.sealedLetter
            )
            val msgType = normalizeInvitationMessageTypeForLocal(
                sourceLandId = organizerLandId,
                targetLandId = targetLand,
                requestedType = requestedType
            )

            sentIds += sendTrackedMessage(
                sourceLandId = organizerLandId,
                targetLandId = targetLand,
                fromActorId = draft.organizerActorId,
                toActorId = guestPlan.actorId,
                toRole = null,
                messageType = msgType,
                delivery = guestPlan.deliveryMethod,
                body = OrderComposer.compose(
                    MessageDraft(
                        messageType = msgType,
                        orderKind = OrderKind.INVITE_TO_BALL,
                        actorIds = listOf(guestPlan.actorId),
                        targetLandId = draft.targetLandId,
                        structureIds = listOf(venueId),
                        scheduledTurn = eventTurn,
                        freeTextNote = generalInvitationText
                    )
                ),
                sealed = isMessageSealed(msgType)
            )
        }

        return sentIds
    }

    private suspend fun overwriteExistingBallPlan(
        existingBallId: Long,
        draft: BallPlanDraft
    ): Long {
        val existing = plannedBallDao.byId(existingBallId)
            ?: error("Ball plan $existingBallId not found")

        val oldBardsByActorId = plannedBallBardDao.forBall(existingBallId)
            .associateBy { it.bardActorId }

        val oldGuestsByActorId = plannedBallGuestDao.forBall(existingBallId)
            .associateBy { it.actorId }

        val oldAlcoholRows = plannedBallAlcoholDao.forBall(existingBallId)

        val oldAlcoholByKey = oldAlcoholRows.associateBy {
            "${it.sourceType}:${it.code}:${it.sourceLandId}"
        }

        plannedBallBardDao.deleteForBall(existingBallId)
        plannedBallGuestDao.deleteForBall(existingBallId)
        plannedBallAlcoholDao.deleteForBall(existingBallId)

        val delegated = isDelegatedBallDraft(draft)
        val wasDelegated = existing.organizerActorId != existing.orderedByActorId
        val normalizedScale = normalizeBallScale(draft.scale) ?: BALL_SCALE_MIDDLE
        val newEventTurn = draft.eventTurn ?: existing.eventTurn

        val resetCoinHolderOrder =
            existing.budgetUsesCoinHolder != draft.budgetUsesCoinHolder ||
                    existing.coinHolderOrderDeliveryMethod != draft.coinHolderOrderDeliveryMethod ||
                    existing.coinHolderOrderSendLater != draft.coinHolderOrderSendLater ||
                    existing.scale != normalizedScale ||
                    existing.eventTurn != newEventTurn ||
                    existing.venueStructureId != draft.venueStructureId

        val resetDelegationOrder =
            wasDelegated != delegated ||
                    existing.organizerActorId != draft.organizerActorId ||
                    existing.delegationOrderDeliveryMethod != draft.delegationOrderDeliveryMethod ||
                    existing.delegationOrderSendLater != draft.delegationOrderSendLater ||
                    existing.scale != normalizedScale ||
                    existing.eventTurn != newEventTurn ||
                    existing.venueStructureId != draft.venueStructureId

        plannedBallDao.upsert(
            existing.copy(
                orderedByActorId = draft.orderedByActorId,
                organizerActorId = draft.organizerActorId,
                landId = draft.targetLandId,
                eventTurn = newEventTurn,
                venueStructureId = draft.venueStructureId,
                inviteLocalPeople = draft.inviteLocalPeople,
                localInviteMinimumTitleRank = draft.localInviteMinimumTitleRank,
                localMusicianCount = if (delegated) 0 else draft.localMusicianCount,
                scale = normalizedScale,
                useAlcohol = if (delegated) draft.useAlcohol else false,
                budgetUsesCoinHolder = draft.budgetUsesCoinHolder,
                coinHolderOrderDeliveryMethod = draft.coinHolderOrderDeliveryMethod,
                coinHolderOrderSendLater = draft.coinHolderOrderSendLater,
                delegationOrderDeliveryMethod = draft.delegationOrderDeliveryMethod,
                delegationOrderSendLater = draft.delegationOrderSendLater,
                coinHolderOrderSent = if (resetCoinHolderOrder) false else existing.coinHolderOrderSent,
                coinHolderOrderDelivered = if (resetCoinHolderOrder) false else existing.coinHolderOrderDelivered,
                delegationOrderSent = if (resetDelegationOrder || !delegated) false else existing.delegationOrderSent,
                delegationOrderDelivered = if (resetDelegationOrder || !delegated) false else existing.delegationOrderDelivered,
                status = "PLANNING"
            )
        )

        draft.bardPlans.forEach {
            val old = oldBardsByActorId[it.actorId]

            plannedBallBardDao.upsert(
                PlannedBallBardEntity(
                    plannedBallId = existingBallId,
                    bardActorId = it.actorId,
                    invitationSent = if (delegated) false else (old?.invitationSent ?: it.invitationSent),
                    accepted = if (delegated) null else old?.accepted,
                    deliveryMethod = if (delegated) DeliveryMethod.MESSENGER else it.deliveryMethod,
                    messageType = if (delegated) {
                        MessageType.SEALED_LETTER
                    } else {
                        resolveInvitationMessageType(
                            oral = it.oral,
                            sealedLetter = it.sealedLetter
                        )
                    },
                    feeSilver = if (delegated) 0 else it.feeSilver,
                    notabilityLevel = it.notabilityLevel,
                    generatedLocally = if (delegated) false else it.generatedLocally,
                    sendLater = if (delegated) true else it.sendLater
                )
            )
        }

        draft.attendeePlans.forEach {
            val old = oldGuestsByActorId[it.actorId]

            plannedBallGuestDao.upsert(
                PlannedBallGuestEntity(
                    plannedBallId = existingBallId,
                    actorId = it.actorId,
                    invitationSent = if (delegated) false else (old?.invitationSent ?: it.invitationSent),
                    accepted = if (delegated) null else old?.accepted,
                    deliveryMethod = if (delegated) DeliveryMethod.MESSENGER else it.deliveryMethod,
                    messageType = if (delegated) {
                        MessageType.SEALED_LETTER
                    } else {
                        resolveInvitationMessageType(
                            oral = it.oral,
                            sealedLetter = it.sealedLetter
                        )
                    },
                    sendLater = if (delegated) true else it.sendLater
                )
            )
        }

        if (!delegated) {
            draft.alcoholPlan.fromStocks.forEach {
                val old = oldAlcoholByKey["STOCK:${it.code}:${it.landId}"]

                plannedBallAlcoholDao.upsert(
                    PlannedBallAlcoholEntity(
                        plannedBallId = existingBallId,
                        code = it.code,
                        quantity = it.quantity,
                        sourceLandId = it.landId,
                        sourceType = "STOCK",
                        transportDeliveryMethod = it.deliveryMethod,
                        transportSealedLetter = it.deliveryMethod != DeliveryMethod.LOCAL,
                        transportSendLater = it.sendLater,
                        transportOrderSent = old?.transportOrderSent ?: it.orderSent,
                        transportOrderDelivered = old?.transportOrderDelivered ?: it.orderDelivered
                    )
                )
            }

            draft.alcoholPlan.fromMarket.forEach {
                val old = oldAlcoholByKey["MARKET:${it.code}:${it.marketLandId}"]

                plannedBallAlcoholDao.upsert(
                    PlannedBallAlcoholEntity(
                        plannedBallId = existingBallId,
                        code = it.code,
                        quantity = it.quantity,
                        sourceLandId = it.marketLandId,
                        sourceType = "MARKET",
                        unitPrice = it.unitPrice,
                        transportDeliveryMethod = it.transportDelivery,
                        transportSealedLetter = it.transportSealed,
                        transportSendLater = false,
                        transportOrderSent = old?.transportOrderSent ?: false,
                        transportOrderDelivered = old?.transportOrderDelivered ?: false
                    )
                )
            }

            draft.budgetFromStocks.forEach {
                val old = oldAlcoholByKey["BUDGET:$MONEY_CODE:${it.landId}"]

                plannedBallAlcoholDao.upsert(
                    PlannedBallAlcoholEntity(
                        plannedBallId = existingBallId,
                        code = MONEY_CODE,
                        quantity = it.quantity,
                        sourceLandId = it.landId,
                        sourceType = "BUDGET",
                        transportDeliveryMethod = it.deliveryMethod,
                        transportSealedLetter = it.deliveryMethod != DeliveryMethod.LOCAL,
                        transportSendLater = it.sendLater,
                        transportOrderSent = old?.transportOrderSent ?: it.orderSent,
                        transportOrderDelivered = old?.transportOrderDelivered ?: it.orderDelivered
                    )
                )
            }
        }

        updateBallPlanReadiness(existingBallId)
        return existingBallId
    }

    suspend fun requestNobleReport(role: String, via: DeliveryMethod) {
        post.send(
            from = session.actorId,
            toActorId = null,
            toRole = role,
            messageType = if (via == DeliveryMethod.LOCAL) MessageType.ORAL else MessageType.LETTER,
            delivery = via,
            body = "REQUEST:REPORT:LAND=${session.currentLandId}",
            turn = turns.read().turn,
            sealed = (via != DeliveryMethod.LOCAL)
        )
    }

    suspend fun getKnownArmyList(
        landId: Long,
        autoconfirm: Boolean
    ): ArmyListActionState {
        val nowTurn = turns.read().turn
        val facts = knowledge.lastKnownArmyLandByFaction(session.actorId)
            .filter { it.landId == landId }

        val moves = knowledge.knownMovements(session.actorId).associateBy { it.armyId }

        val armies = facts.map { f ->
            val mv = moves[f.armyId]
            val eta = mv?.let {
                val edges = (it.path.size - 1).coerceAtLeast(0)
                val elapsed = (nowTurn - it.createdTurn).coerceAtLeast(0)
                val spd = speeds.speedPerTurn(it.armyId)
                val remain = (edges - elapsed * spd).toDouble()
                if (remain >0) kotlin.math.ceil(remain).toInt() else null
            }

            ArmyInfo(
                armyId = f.armyId,
                name = null, // later you can join ArmyDao for real names
                faction = FactionId.of(f.factionIndex),
                lastKnownLandId = f.landId,
                movingEta = eta
            )
        }

        val defenseCommanderLand = knownRoleHolderLand("DEFENSE_COMMANDER")
        val localRefresh = defenseCommanderLand != null && defenseCommanderLand == session.currentLandId

        return ArmyListActionState(
            armies = armies,
            canRefreshLocally = localRefresh,
            refreshRole = "DEFENSE_COMMANDER"
        )
    }

    suspend fun refreshArmyListLocally(landId: Long): List<ArmyInfo> {
        requestFreshArmyList(
            landId = landId,
            messageType = MessageType.ORAL,
            via = DeliveryMethod.LOCAL
        )
        return getKnownArmyList(landId, autoconfirm = true).armies
    }

    // -- MOVE ARMY (message vs local)
    suspend fun movePlayerTo(landId: Long) {
        val draft = MessageDraft(
            messageType = MessageType.ORAL,
            orderKind = OrderKind.MOVE_PLAYER_TO,
            targetLandId = landId
        )

        sendDraftToRole("PLAYER", draft, DeliveryMethod.LOCAL)
        session.currentLandId = landId
    }

    suspend fun getKnownArmiesInLand(landId: Long): List<ArmyKnowledge> {
        val all = knowledge.knownArmiesForActor(session.actorId)
        return all.filter { it.landId == landId }
    }


    suspend fun orderMoveArmy(armyId: Long, toLandId: Long, via: DeliveryMethod) {
        val draft = MessageDraft(
            messageType = messageTypeFor(via),
            orderKind = OrderKind.MOVE_ARMY_TO_LAND,
            armyIds = listOf(armyId),
            targetLandId = toLandId,
        )

        sendDraftToRole("DEFENSE_COMMANDER", draft, via)
    }

    suspend fun improveDefenseCommand(
        landId: Long,
        structureType: DefenseStructureType,
        via: DeliveryMethod
    ) {
        val draft = MessageDraft(
            messageType = messageTypeFor(via),
            orderKind = OrderKind.IMPROVE_DEFENSE,
            targetLandId = landId,
            defenseStructureType = structureType
        )

        sendDraftToRole("DEFENSE_COMMANDER", draft, via)
    }

    suspend fun organizeRebellion(landId: Long, via: DeliveryMethod) {
        val draft = MessageDraft(
            messageType = messageTypeFor(via),
            orderKind = OrderKind.ORGANIZE_REBELLION,
            targetLandId = landId
        )

        sendDraftToRole("CHANCELLOR", draft, via)
    }

    suspend fun organizeBall(landId: Long, via: DeliveryMethod) {
        val draft = MessageDraft(
            messageType = messageTypeFor(via),
            orderKind = OrderKind.ORGANIZE_BALL,
            targetLandId = landId
        )

        sendDraftToRole("CHANCELLOR", draft, via)
    }

    suspend fun organizeHunt(landId: Long, via: DeliveryMethod) {
        val draft = MessageDraft(
            messageType = messageTypeFor(via),
            orderKind = OrderKind.ORGANIZE_HUNT,
            targetLandId = landId
        )

        sendDraftToRole("CHANCELLOR", draft, via)
    }

    suspend fun organizeFestival(landId: Long, religious: Boolean, via: DeliveryMethod) {
        val kind = if (religious) {
            OrderKind.ORGANIZE_RELIGIOUS_FESTIVAL
        } else {
            OrderKind.ORGANIZE_FESTIVAL
        }

        val draft = MessageDraft(
            messageType = messageTypeFor(via),
            orderKind = kind,
            targetLandId = landId
        )

        sendDraftToRole("CHANCELLOR", draft, via)
    }

    suspend fun listActorsAtLandFromKnowledge(landId: Long): List<KnownActorAtLand> {
        // You need a DAO that reads knowledge entries for an actor.
        // Example: db.KnowledgeDao().getTrueForActor(session.actorId)
        // If your DAO differs, adapt the call.

        val facts: List<KnowledgeEntryEntity> =
            knowledgeDao.getTrueKnowledgeForActor(session.actorId) // TODO: adapt

        // Parse actor locations
        val locRegex = Regex("""^ACTOR_AT:(\d+):LAND=(\d+):IMPRISONED=(0|1)$""")
        val nameRegex = Regex("""^ACTOR_NAME:(\d+):(.+)$""")

        val lastLocation = mutableMapOf<Long, Triple<Long, Boolean?, Int>>() // actorId -> (landId, imprisoned, turn)
        val names = mutableMapOf<Long, String>()

        for (k in facts) {
            val f = k.fact.trim()

            nameRegex.matchEntire(f)?.let { m ->
                val actorId = m.groupValues[1].toLong()
                val name = m.groupValues[2]
                // keep newest name by turn
                val prevTurn = lastLocation[actorId]?.third ?: -1
                if (k.turn >= prevTurn) names[actorId] = name
                return@let
            }

            locRegex.matchEntire(f)?.let { m ->
                val actorId = m.groupValues[1].toLong()
                val lId = m.groupValues[2].toLong()
                val imprisoned = (m.groupValues[3] == "1")
                val prev = lastLocation[actorId]
                if (prev == null || k.turn >= prev.third) {
                    lastLocation[actorId] = Triple(lId, imprisoned, k.turn)
                }
            }
        }

        return lastLocation
            .filter { (_, triple) -> triple.first == landId }
            .map { (actorId, triple) ->
                KnownActorAtLand(
                    actorId = actorId,
                    name = names[actorId],
                    imprisoned = triple.second,
                    lastSeenTurn = triple.third
                )
            }
            .sortedByDescending { it.lastSeenTurn }
    }

    // ----- battle -----

    suspend fun battleSendOrderToUnit(unitId: Long, tactic: String, via: DeliveryMethod) {
        val draft = MessageDraft(
            messageType = MessageType.ORAL,
            orderKind = OrderKind.TACTICAL_UNIT_ORDER,
            armyIds = listOf(unitId),
            freeTextNote = tactic
        )

        sendDraftToRole("UNIT_$unitId", draft, via)
    }

    suspend fun battleGeneralCommand(command: String, via: DeliveryMethod) {
        val draft = MessageDraft(
            messageType = MessageType.ORAL,
            orderKind = OrderKind.GENERAL_BATTLE_ORDER,
            freeTextNote = command
        )

        sendDraftToRole("ALL_UNITS", draft, via)
    }

    suspend fun battleMapMoveUnit(unitId: Long, toX: Int, toY: Int, via: DeliveryMethod) {
        val draft = MessageDraft(
            messageType = MessageType.ORAL,
            orderKind = OrderKind.MOVE_BATTLE_UNIT,
            armyIds = listOf(unitId),
            freeTextNote = "$toX,$toY"
        )

        sendDraftToRole("UNIT_$unitId", draft, via)
    }

    // ----- duel -----

    data class DuelOutcome(val code: String, val playerDefeated: Boolean)

    suspend fun duelStart(duelId: Long, opponentId: Long): DuelOutcome {
        val myTraits = DuelOddsCalculator.Traits(strength = 60, caution = 40, superstition = 30, beauty = 50)
        val oppTraits = DuelOddsCalculator.Traits(strength = 55, caution = 35, superstition = 40, beauty = 40)
        val pWin = duelOdds.winChance(myTraits, oppTraits)
        val roll = rng.nextInt(100)
        val outcome = when {
            roll < pWin -> "WIN_A"
            roll < 95   -> "WIN_B"
            else        -> "DRAW"
        }
        dignity.finalizeDuel(
            duel = com.example.mygame.database.dignity_duels_conflicts.DuelEntity(
                id = duelId, participantA = session.actorId, participantB = opponentId,
                turn = turns.read().turn, outcome = outcome
            ),
            outcome = outcome
        )
        return DuelOutcome(outcome, playerDefeated = (outcome == "WIN_B"))
    }

    suspend fun duelTryToAvoid(duelId: Long, opponentId: Long): Boolean {
        val succeed = rng.nextInt(100) < 40
        if (succeed) {
            dignity.finalizeDuel(
                duel = com.example.mygame.database.dignity_duels_conflicts.DuelEntity(
                    id = duelId, participantA = session.actorId, participantB = opponentId,
                    turn = turns.read().turn, outcome = "AVOIDED"
                ),
                outcome = "AVOIDED"
            )
            return true
        }
        // Failed to avoid → will need to start actual duel in controller
        return false
    }

    // ----- social / events -----

    suspend fun ballJoin(ballId: Long) {
        val imp = entertainImpact.forBall(wineBarrels = 2, bardRank = 1, scandal = rng.nextInt(100) < 15)
        audit.log("BALL_JOIN", mapOf("ballId" to ballId, "mood" to imp.moodDelta, "prestige" to imp.prestigeDelta))
    }

    suspend fun ballInviteToDance(targetId: Long) {
        val accept = rng.nextInt(100) < 60
        val delta = if (accept) 1 else -1
        post.send(
            from = session.actorId,
            toActorId = targetId,
            toRole = null,
            messageType = MessageType.ORAL,
            delivery = DeliveryMethod.LOCAL,
            body = "DANCE_INVITE:${if (accept) "YES" else "NO"}",
            turn = turns.read().turn,
            sealed = false
        )
        audit.log("BALL_DANCE", mapOf("target" to targetId, "delta" to delta))
    }

    data class HuntResult(val playerDied: Boolean)

    suspend fun huntJoin(huntId: Long): HuntResult {
        val bestKill = rng.nextInt(6)
        val injuries = if (rng.nextInt(100) < 10) 1 else 0
        val imp = entertainImpact.forHunt(bestKill, injuries)
        audit.log("HUNT_JOIN", mapOf("huntId" to huntId, "bestKill" to bestKill, "injuries" to injuries, "mood" to imp.moodDelta))
        val died = injuries > 0 && rng.nextInt(100) < 5
        return HuntResult(playerDied = died)
    }

    suspend fun festivalJoin(festivalId: Long, religious: Boolean) {
        val imp = entertainImpact.forFestival(visitingLeader = rng.nextInt(100) < 10, beerBarrels = 2)
        audit.log("FESTIVAL_JOIN", mapOf("festivalId" to festivalId, "religious" to religious, "mood" to imp.moodDelta))
    }

    // ----- tavern / messaging -----

    suspend fun tavernShow(type: String) {
        post.send(
            from = session.actorId,
            toActorId = null,
            toRole = "BARMEN",
            messageType = MessageType.ORAL,
            delivery = DeliveryMethod.LOCAL,
            body = "REQUEST:SHOW:$type",
            turn = turns.read().turn,
            sealed = false
        )
    }

    suspend fun tavernHire(type: String, targetId: Long?) {
        val role = when (type) {
            "spy", "assassin" -> "CHANCELLOR"
            "tax_collector", "merchant" -> "COIN_HOLDER"
            "recruiting_agent" -> "DEFENSE_COMMANDER"
            else -> "CHANCELLOR"
        }

        val draft = MessageDraft(
            messageType = MessageType.ORAL,
            orderKind = OrderKind.HIRE_AGENT,
            actorIds = targetId?.let { listOf(it) } ?: emptyList(),
            freeTextNote = type
        )

        sendDraftToRole(role, draft, DeliveryMethod.LOCAL)
    }

    suspend fun tavernBuyFood(item: String, qty: Int) {
        post.send(
            from = session.actorId,
            toActorId = null,
            toRole = "BARMEN",
            messageType = MessageType.ORAL,
            delivery = DeliveryMethod.LOCAL,
            body = "BUY:$item:$qty",
            turn = turns.read().turn,
            sealed = false
        )
    }

    // ----- justice -----

    suspend fun judgeVerdict(crimeId: Long, guilty: Boolean) {
        val trialId = justice.openTrial(crimeId, judgeNobleId = session.actorId, startedTurn = turns.read().turn)
        justice.setVerdict(trialId, guilty, turn = turns.read().turn)
    }

    suspend fun arrestAttemptBribeJudge(amount: Int): Boolean {
        val success = rng.nextInt(100) < (30 + amount / 1000)
        audit.log("BRIBE_JUDGE", mapOf("amount" to amount, "success" to success))
        return success
    }

    suspend fun arrestAttemptBribeWitness(witnessId: Long, amount: Int): Boolean {
        val success = rng.nextInt(100) < (20 + amount / 1000)
        audit.log("BRIBE_WITNESS", mapOf("witness" to witnessId, "amount" to amount, "success" to success))
        return success
    }

    /** returns true if player is defeated (non-fine guilty path) */
    suspend fun onArrestVerdictReceived(guilty: Boolean, punishment: String?): Boolean {
        return guilty && punishment != "FINE"
    }

    // ----- meal -----

    suspend fun mealStart(menu: List<String>): Boolean {
        post.send(
            from = session.actorId,
            toActorId = null,
            toRole = "PLAYER",
            messageType = MessageType.ORAL,
            delivery = DeliveryMethod.LOCAL,
            body = "MEAL_START:${menu.joinToString(",")}",
            turn = turns.read().turn,
            sealed = false
        )
        val poisoned = rng.nextInt(100) < 5
        return poisoned
    }

    // ----- main menu -----

    suspend fun startNewGameFromMenu(scenarioId: Long, confirmDrop: Boolean): Boolean {
        if (!confirmDrop && session.scenarioId != null) return false
        session.scenarioId = scenarioId
        audit.log("SCENARIO_START", mapOf("scenario" to scenarioId))
        return true
    }

    // =============================
// Global map snapshot
// =============================
    suspend fun getGlobalMapSnapshot(viewWidth: Int, viewHeight: Int): MapSnapshot {
        // 1) Pre-compute settlement anchors
        val lands = LandDao.getAll() // TODO: adapt to your DAO method
        val settlements = lands.map { l ->
            val (x, y) = layout.landAnchor(l.id, viewWidth, viewHeight)
            SettlementMarker(landId = l.id, screenX = x, screenY = y)
        }

        // 2) Build flags from KNOWLEDGE + assumptions
        val nowTurn = turns.read().turn
        val armyFacts   = knowledge.lastKnownArmyLandByFaction(session.actorId)
        val garrison    = knowledge.knownGarrisons(session.actorId)
        val sieges      = knowledge.knownSieges(session.actorId)
        val movements   = knowledge.knownMovements(session.actorId)

        // Compute moving positions (edge midpoint or ETA snap)
        val movingByArmy: MutableMap<Long, FlagMarker> = mutableMapOf()
        movements.forEach { mv ->
            val elapsed = (nowTurn - mv.createdTurn).coerceAtLeast(0)
            val speed   = speeds.speedPerTurn(mv.armyId).coerceAtLeast(0.001f)
            val pathEdges = (0 until (mv.path.size - 1)).map { i -> mv.path[i] to mv.path[i+1] }
            val progressed = elapsed * speed

            if (progressed >= pathEdges.size) {
                // Arrived (we’ll let it merge with NORMAL flags below by land)
                return@forEach
            }
            val edgeIndex = progressed.toInt().coerceIn(0, pathEdges.lastIndex)
            val (from, to) = pathEdges[edgeIndex]
            val (ex, ey) = layout.borderMidpoint(from, to, viewWidth, viewHeight)
            val faction = armyFacts.firstOrNull { it.armyId == mv.armyId }?.factionIndex ?: 7
            val etaTurns = kotlin.math.ceil(pathEdges.size - progressed).toInt().coerceAtLeast(1)
            movingByArmy[mv.armyId] = FlagMarker(
                key = "mv_${mv.armyId}",
                landId = to,
                faction = FactionId.of(faction),
                kind = FlagKind.MOVING,
                screenX = ex,
                screenY = ey,
                aggregatedArmyCount = 1,
                label = "ETA ${etaTurns}t"
            )
        }

        // Group parked/arrived armies by (landId, faction)
        data class Key(val land: Long, val faction: Int)
        val grouped = mutableMapOf<Key, Int>() // count armies
        armyFacts.forEach { a ->
            // If it is moving right now (and not yet at destination), skip NORMAL for now
            if (movingByArmy.containsKey(a.armyId)) return@forEach
            val k = Key(a.landId, a.factionIndex)
            grouped[k] = (grouped[k] ?: 0) + 1
        }
        // Merge garrisons as an extra count
        garrison.forEach { g ->
            val k = Key(g.landId, g.factionIndex)
            grouped[k] = (grouped[k] ?: 0) + g.count
        }

        val normalFlags = grouped.map { (k, count) ->
            val (x, y) = layout.landAnchor(k.land, viewWidth, viewHeight)
            FlagMarker(
                key = "ln_${k.land}_${k.faction}",
                landId = k.land,
                faction = FactionId.of(k.faction),
                kind = FlagKind.NORMAL,
                screenX = x,
                screenY = y,
                aggregatedArmyCount = count
            )
        }

        // Siege attacker flags: shown near city; multiple attackers OK
        val siegeFlags = sieges.map { s ->
            val (x, y) = layout.landAnchor(s.defenderLandId, viewWidth, viewHeight)
            // offset slightly to avoid overlap (you can improve with per-faction offset ring)
            val dx = (s.attackerFactionIndex % 3 - 1) * 18
            val dy = (s.attackerFactionIndex / 3 - 1) * 18
            FlagMarker(
                key = "sg_${s.defenderLandId}_${s.attackerFactionIndex}",
                landId = s.defenderLandId,
                faction = FactionId.of(s.attackerFactionIndex),
                kind = FlagKind.SIEGE_ATTACKER,
                screenX = x + dx,
                screenY = y + dy,
                aggregatedArmyCount = 1
            )
        }

        // Moving flags add on top
        val movingFlags = movingByArmy.values.toList()

        val (px, py) = layout.landAnchor(session.currentLandId, viewWidth, viewHeight)
        val playerFlag = FlagMarker(
            key = "player_${session.actorId}",
            landId = session.currentLandId,
            faction = FactionId.of(0), // value not important for player marker
            kind = FlagKind.PLAYER,
            screenX = px,
            screenY = py,
            aggregatedArmyCount = 1,
            label = null
        )



        return MapSnapshot(
            settlements = settlements,
            flags = normalFlags + siegeFlags + movingFlags + playerFlag
        )
    }

    suspend fun getKnownLandReport(
        landId: Long,
        autoconfirm: Boolean
    ): LandReportActionState {
        val facts = knowledgeDao.getTrueKnowledgeForActor(session.actorId)

        var population: Int? = null
        var satisfaction: Int? = null
        var foodTurns: Int? = null
        var state: String? = null

        val resources = linkedMapOf<String, Int>()
        val events = mutableListOf<Pair<String, Int>>()

        var bestPopulationTurn = -1
        var bestSatisfactionTurn = -1
        var bestFoodTurn = -1
        var bestStateTurn = -1
        val resourceTurns = mutableMapOf<String, Int>()

        val popRegex = Regex("""^LAND_POPULATION:(\d+):TOTAL=(\d+)$""")
        val satRegex = Regex("""^LAND_SATISFACTION:(\d+):LEVEL=(\d+)$""")
        val foodRegex = Regex("""^LAND_FOOD_TURNS:(\d+):VALUE=(\d+)$""")
        val stateRegex = Regex("""^LAND_STATE:(\d+):VALUE=([A-Z_]+)$""")
        val resRegex = Regex("""^LAND_RESOURCE:(\d+):ITEM=([A-Z_]+):QTY=(\d+)$""")
        val eventRegex = Regex("""^LAND_EVENT:(\d+):TYPE=([A-Z_]+):TURN=(\d+)$""")

        for (k in facts) {
            val fact = k.fact.trim()

            popRegex.matchEntire(fact)?.let { m ->
                if (m.groupValues[1].toLong() == landId && k.turn >= bestPopulationTurn) {
                    population = m.groupValues[2].toInt()
                    bestPopulationTurn = k.turn
                }
            }

            satRegex.matchEntire(fact)?.let { m ->
                if (m.groupValues[1].toLong() == landId && k.turn >= bestSatisfactionTurn) {
                    satisfaction = m.groupValues[2].toInt()
                    bestSatisfactionTurn = k.turn
                }
            }

            foodRegex.matchEntire(fact)?.let { m ->
                if (m.groupValues[1].toLong() == landId && k.turn >= bestFoodTurn) {
                    foodTurns = m.groupValues[2].toInt()
                    bestFoodTurn = k.turn
                }
            }

            stateRegex.matchEntire(fact)?.let { m ->
                if (m.groupValues[1].toLong() == landId && k.turn >= bestStateTurn) {
                    state = m.groupValues[2]
                    bestStateTurn = k.turn
                }
            }

            resRegex.matchEntire(fact)?.let { m ->
                if (m.groupValues[1].toLong() == landId) {
                    val item = m.groupValues[2]
                    val qty = m.groupValues[3].toInt()
                    val oldTurn = resourceTurns[item] ?: -1
                    if (k.turn >= oldTurn) {
                        resources[item] = qty
                        resourceTurns[item] = k.turn
                    }
                }
            }

            eventRegex.matchEntire(fact)?.let { m ->
                if (m.groupValues[1].toLong() == landId) {
                    events += m.groupValues[2] to m.groupValues[3].toInt()
                }
            }
        }

        val report = ReportResult(
            landId = landId,
            population = population,
            satisfaction = satisfaction,
            resources = resources,
            foodTurns = foodTurns,
            state = state,
            events = events.sortedBy { it.second }
        )

        val chancellorLand = knownRoleHolderLand("CHANCELLOR")
        val localRefresh = (chancellorLand != null && chancellorLand == session.currentLandId)

        return LandReportActionState(
            report = report,
            canRefreshLocally = localRefresh,
            refreshRole = "CHANCELLOR"
        )
    }

    suspend fun getActualActorLand(actorId: Long): Long? {
        return actualActorLand(actorId)
    }

    private suspend fun invalidateKnownActorLocationIfObservedAbsent(
        viewerActorId: Long,
        targetActorId: Long,
        observedLandId: Long
    ) {
        if (viewerActorId <= 0L || targetActorId <= 0L) return

        val actualLandId = actualActorLand(targetActorId)

        // If actor is actually here → nothing to invalidate
        if (actualLandId == observedLandId) return

        // Mark knowledge as unknown
        markKnownActorLocationUnknown(
            viewerActorId = viewerActorId,
            targetActorId = targetActorId
        )
    }

    suspend fun requestFreshRoleInfo(
        role: String,
        body: String,
        via: DeliveryMethod
    ) {
        post.send(
            from = session.actorId,
            toActorId = null,
            toRole = role,
            messageType = if (via == DeliveryMethod.LOCAL) MessageType.ORAL else MessageType.LETTER,
            delivery = via,
            body = body,
            turn = turns.read().turn,
            sealed = (via != DeliveryMethod.LOCAL)
        )
    }

    suspend fun requestFreshRoleInfo(
        role: String,
        body: String,
        messageType: MessageType,
        via: DeliveryMethod
    ) {
        post.send(
            from = session.actorId,
            toActorId = null,
            toRole = role,
            messageType = messageType,
            delivery = via,
            body = body,
            turn = turns.read().turn,
            sealed = (messageType == MessageType.SEALED_LETTER)
        )
    }

    suspend fun requestFreshLandReport(
        landId: Long,
        messageType: MessageType,
        via: DeliveryMethod
    ) {
        val draft = MessageDraft(
            messageType = messageType,
            orderKind = OrderKind.REQUEST_LAND_REPORT,
            targetLandId = landId
        )

        sendDraftToRole("CHANCELLOR", draft, via)
    }

    suspend fun requestFreshActorsAtLand(
        landId: Long,
        messageType: MessageType,
        via: DeliveryMethod
    ) {
        val draft = MessageDraft(
            messageType = messageType,
            orderKind = OrderKind.REQUEST_ACTOR_LIST,
            targetLandId = landId
        )

        sendDraftToRole("CHANCELLOR", draft, via)
    }

    suspend fun refreshLandReportLocally(landId: Long): ReportResult {
        val chancellorActorId = getCurrentChancellorActorId()
        if (chancellorActorId != null && chancellorActorId > 0L) {
            invalidateKnownActorLocationIfObservedAbsent(
                viewerActorId = session.actorId,
                targetActorId = chancellorActorId,
                observedLandId = landId
            )
        }

        requestFreshLandReport(
            landId = landId,
            messageType = MessageType.ORAL,
            via = DeliveryMethod.LOCAL
        )
        return getKnownLandReport(landId, autoconfirm = true).report
    }

    suspend fun requestFreshArmyList(
        landId: Long,
        messageType: MessageType,
        via: DeliveryMethod
    ) {
        val draft = MessageDraft(
            messageType = messageType,
            orderKind = OrderKind.REQUEST_ARMY_LIST,
            targetLandId = landId
        )

        sendDraftToRole("DEFENSE_COMMANDER", draft, via)
    }

    suspend fun getKnownActorList(
        landId: Long,
        autoconfirm: Boolean
    ): ActorListActionState {
        val domain = listActorsAtLandFromKnowledge(landId)

        val actors = domain.map {
            ActorView(
                actorId = it.actorId,
                name = it.name ?: "Unknown #${it.actorId}",
                imprisoned = it.imprisoned ?: false
            )
        }

        val chancellorLand = knownRoleHolderLand("CHANCELLOR")
        val localRefresh = chancellorLand != null && chancellorLand == session.currentLandId

        return ActorListActionState(
            actors = actors,
            canRefreshLocally = localRefresh,
            refreshRole = "CHANCELLOR"
        )
    }

    suspend fun refreshActorListLocally(landId: Long): List<ActorView> {
        val chancellorActorId = getCurrentChancellorActorId()
        if (chancellorActorId != null && chancellorActorId > 0L) {
            invalidateKnownActorLocationIfObservedAbsent(
                viewerActorId = session.actorId,
                targetActorId = chancellorActorId,
                observedLandId = landId
            )
        }

        requestFreshActorsAtLand(landId, MessageType.ORAL, DeliveryMethod.LOCAL)
        return getKnownActorList(landId, autoconfirm = true).actors
    }

    private suspend fun knownActorLand(
        viewerActorId: Long,
        actorId: Long
    ): Long? {
        val facts = knowledgeDao.getTrueKnowledgeForActor(viewerActorId)

        val atRegex = Regex("""^ACTOR_AT:(\d+):LAND=(\d+):IMPRISONED=(0|1)$""")
        val unknownRegex = Regex("""^ACTOR_LOCATION_UNKNOWN:(\d+)$""")

        var bestKnownTurn = -1
        var bestKnownLand: Long? = null
        var bestUnknownTurn = -1

        for (k in facts) {
            val fact = k.fact.trim()

            val at = atRegex.matchEntire(fact)
            if (at != null) {
                val factActorId = at.groupValues[1].toLong()
                val landId = at.groupValues[2].toLong()

                if (factActorId == actorId && k.turn >= bestKnownTurn) {
                    bestKnownTurn = k.turn
                    bestKnownLand = landId
                }
                continue
            }

            val unknown = unknownRegex.matchEntire(fact)
            if (unknown != null) {
                val factActorId = unknown.groupValues[1].toLong()
                if (factActorId == actorId && k.turn >= bestUnknownTurn) {
                    bestUnknownTurn = k.turn
                }
            }
        }

        return if (bestUnknownTurn >= bestKnownTurn) null else bestKnownLand
    }

    private suspend fun getKnownMoneyStorageLand(
        viewerActorId: Long,
        targetLandId: Long,
        requiredAmount: Int
    ): Long? {
        if (requiredAmount <= 0) return targetLandId

        if (localSilverAtLand(targetLandId) >= requiredAmount) {
            return targetLandId
        }

        val facts = knowledgeDao.getTrueKnowledgeForActor(viewerActorId)
        val patterns = listOf(
            Regex("""^LAND_TREASURY:(\d+):SILVER=(\d+)$"""),
            Regex("""^LAND_COINS:(\d+):SILVER=(\d+)$"""),
            Regex("""^LAND_SILVER:(\d+):QTY=(\d+)$"""),
            Regex("""^LAND_RESOURCE:(\d+):ITEM=SILVER:QTY=(\d+)$""")
        )

        var bestLandId: Long? = null
        var bestQty = -1
        var bestTurn = -1

        for (k in facts) {
            val fact = k.fact.trim()

            for (pattern in patterns) {
                val m = pattern.matchEntire(fact) ?: continue
                val landId = m.groupValues[1].toLong()
                val qty = m.groupValues[2].toInt()

                if (qty < requiredAmount) continue

                val isBetter =
                    qty > bestQty ||
                            (qty == bestQty && k.turn > bestTurn)

                if (isBetter) {
                    bestLandId = landId
                    bestQty = qty
                    bestTurn = k.turn
                }
            }
        }

        return bestLandId ?: knownActorLand(
            viewerActorId = viewerActorId,
            actorId = viewerActorId
        ) ?: session.currentLandId
    }

    private suspend fun knownActorLand(actorId: Long): Long? {
        return knownActorLand(
            viewerActorId = session.actorId,
            actorId = actorId
        )
    }

    private suspend fun knownRoleHolderLand(roleName: String): Long? {
        return knownRoleHolderActorId(roleName)?.let { knownActorLand(it) }
    }

    suspend fun startMessageComposer(
        messageType: MessageType,
        orderKind: OrderKind
    ): MessageComposerState {
        val draft = MessageDraft(
            messageType = messageType,
            orderKind = orderKind
        )
        return MessageComposerState(
            orderKind = orderKind,
            requiredFields = OrderRequirements.requiredFields(orderKind),
            draft = draft
        )
    }

    suspend fun composeOutgoingMessage(draft: MessageDraft): String {
        return OrderComposer.compose(draft)
    }

    suspend fun sendStructuredOrder(
        toRole: String?,
        toActorId: Long?,
        draft: MessageDraft,
        delivery: DeliveryMethod,
        sealed: Boolean
    ): Long {
        val payload = OrderComposer.compose(draft)

        return post.send(
            from = session.actorId,
            toActorId = toActorId,
            toRole = toRole,
            messageType = draft.messageType,
            delivery = delivery,
            body = payload,
            turn = turns.read().turn,
            sealed = sealed
        )
    }

    //method for testing
    suspend fun requestLandReport(landId: Long, via: DeliveryMethod) {
        requestFreshLandReport(
            landId = landId,
            messageType = when (via) {
                DeliveryMethod.LOCAL -> MessageType.ORAL
                DeliveryMethod.MESSENGER -> MessageType.LETTER
                DeliveryMethod.DOVE -> MessageType.LETTER
            },
            via = via
        )
    }
    // =============================
    // Context menu
    // =============================
    suspend fun getMapMenuFor(landId: Long, selectedFaction: FactionId?): List<MapMenuItem> {
        val owned = OwnershipDao.isOwnedByPlayerOrVassal(landId)
        val playerHere = (session.currentLandId == landId)
        val canOrganizeBall = owned

        return buildList {
            add(MapMenuItem(MapMenuAction.REPORT, true, "Report"))
            add(MapMenuItem(MapMenuAction.LIST_ARMIES, true, "List armies"))
            add(MapMenuItem(MapMenuAction.MOVE_ARMY_HERE, true, "Order army here"))
            add(MapMenuItem(MapMenuAction.MOVE_PLAYER_HERE, !playerHere, "Move me here"))
            add(MapMenuItem(MapMenuAction.IMPROVE_DEFENSE, owned, "Improve defenses"))
            add(MapMenuItem(MapMenuAction.ORGANIZE_BALL, canOrganizeBall, "Organize ball"))
            add(MapMenuItem(MapMenuAction.ORGANIZE_HUNT, owned, "Organize hunt"))
            add(MapMenuItem(MapMenuAction.ORGANIZE_FESTIVAL, owned, "Organize festival"))
            add(MapMenuItem(MapMenuAction.ORGANIZE_RELIGIOUS_FESTIVAL, owned, "Organize religious festival"))
            add(MapMenuItem(MapMenuAction.LIST_ACTORS, true, "List actors"))
            add(MapMenuItem(MapMenuAction.CLOSE, true, "Close"))
        }
    }

    private fun messageTypeFor(via: DeliveryMethod): MessageType =
        MessageType.SEALED_LETTER

    private fun sealedFor(messageType: MessageType): Boolean =
        messageType == MessageType.SEALED_LETTER

    private suspend fun sendDraftToRole(
        role: String,
        draft: MessageDraft,
        via: DeliveryMethod
    ): Long {
        return post.send(
            from = session.actorId,
            toActorId = null,
            toRole = role,
            messageType = draft.messageType,
            delivery = via,
            body = OrderComposer.compose(draft),
            turn = turns.read().turn,
            sealed = sealedFor(draft.messageType)
        )
    }

    private suspend fun sendDraftToActor(
        actorId: Long,
        draft: MessageDraft,
        via: DeliveryMethod
    ): Long {
        return post.send(
            from = session.actorId,
            toActorId = actorId,
            toRole = null,
            messageType = draft.messageType,
            delivery = via,
            body = OrderComposer.compose(draft),
            turn = turns.read().turn,
            sealed = sealedFor(draft.messageType)
        )
    }

    private fun requestDraft(
        kind: OrderKind,
        via: DeliveryMethod,
        lands: List<Long> = emptyList(),
        targetLandId: Long? = null,
        actorIds: List<Long> = emptyList(),
        armyIds: List<Long> = emptyList()
    ): MessageDraft {
        return MessageDraft(
            messageType = messageTypeFor(via),
            orderKind = kind,
            lands = lands,
            targetLandId = targetLandId,
            actorIds = actorIds,
            armyIds = armyIds
        )
    }
}