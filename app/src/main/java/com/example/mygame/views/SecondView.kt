package com.example.mygame.views

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.MotionEvent
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.lifecycle.lifecycleScope
import com.example.mygame.R
import com.example.mygame.database.entertainment_and_social.BallPreparationState
import com.example.mygame.database.map.ActorView
import com.example.mygame.database.map.FlagKind
import com.example.mygame.database.map.FlagMarker
import com.example.mygame.database.map.MapMenuAction
import com.example.mygame.database.map.MapSnapshot
import com.example.mygame.database.messaging_and_information.DeliveryMethod
import com.example.mygame.database.messaging_and_information.FreshInfoRequest
import com.example.mygame.database.messaging_and_information.MessageType
import com.example.mygame.database.messaging_and_information.ReportResult
import com.example.mygame.database.politics_diplomacy_succession.FactionDrawables
import com.example.mygame.database.politics_diplomacy_succession.FactionId
import com.example.mygame.engine_and_helpers.GameEngine
import com.example.mygame.engine_and_helpers.entertainment_and_social.AlcoholStockOption
import com.example.mygame.engine_and_helpers.entertainment_and_social.AttendeeCandidate
import com.example.mygame.engine_and_helpers.entertainment_and_social.BallAlcoholPlan
import com.example.mygame.engine_and_helpers.entertainment_and_social.BallPlanDraft
import com.example.mygame.engine_and_helpers.entertainment_and_social.BallPlanningOptions
import com.example.mygame.engine_and_helpers.entertainment_and_social.BardCandidate
import com.example.mygame.engine_and_helpers.entertainment_and_social.BardPlanItem
import com.example.mygame.engine_and_helpers.entertainment_and_social.GuestPlanItem
import com.example.mygame.engine_and_helpers.entertainment_and_social.MarketAlcoholOption
import com.example.mygame.engine_and_helpers.entertainment_and_social.MarketBuy
import com.example.mygame.engine_and_helpers.entertainment_and_social.StockUse
import com.example.mygame.engine_and_helpers.entertainment_and_social.VenueOption
import com.example.mygame.engine_and_helpers.messaging_and_information.CouncilRoleProposal
import com.example.mygame.engine_and_helpers.messaging_and_information.CouncilRoleProposalTarget
import com.example.mygame.engine_and_helpers.messaging_and_information.DefenseStructureType
import com.example.mygame.engine_and_helpers.messaging_and_information.MarriageProposal
import com.example.mygame.engine_and_helpers.messaging_and_information.MessageDraft
import com.example.mygame.engine_and_helpers.messaging_and_information.OrderKind
import com.example.mygame.engine_and_helpers.ui.ActivityHost
import com.example.mygame.engine_and_helpers.ui.BallPlanUiAction
import com.example.mygame.engine_and_helpers.ui.UiActionController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Collections.addAll
import kotlin.coroutines.resume
import javax.inject.Inject
import com.example.mygame.engine_and_helpers.UIProcess

//view for detailed global map
@AndroidEntryPoint
class SecondView : AppCompatActivity() {
    @Inject
    lateinit var controller: UiActionController
    private lateinit var overlay: FrameLayout
    private lateinit var mapView: ImageView
    private var currentSnapshot: MapSnapshot? = null
    private lateinit var uiProcess: UIProcess

    private enum class BallDeliveryChoice {
        PERSONAL_VISIT,
        MESSENGER,
        DOVE,
        SEND_LATER
    }

    override fun onStart() {
        super.onStart()
        ActivityHost.set(this)
    }

    override fun onStop() {
        ActivityHost.set(null)
        super.onStop()
    }

    companion object {
        const val EXTRA_SESSION = "session"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.secondviewlayout)

        uiProcess = UIProcess(this)

        mapView = findViewById(R.id.globalMap)
        overlay = findViewById(R.id.flagLayer)

        val session = intent.getLongExtra(EXTRA_SESSION, -1L)

        overlay.doOnLayout { loadAndRender() }

        val returnToMainMenuButton: ImageButton = findViewById(R.id.returnToMainMenuButton2)
        returnToMainMenuButton.setOnClickListener {
            val intent = Intent(this, TwelfthView::class.java)
            startActivity(intent)
        }

        val returnHomeButton: ImageButton = findViewById(R.id.returnToHomeButton2)
        returnHomeButton.setOnClickListener {
            val intent = Intent(this, FirstView::class.java)
            startActivity(intent)
        }

        overlay.setOnTouchListener { _, event ->
            if (currentSnapshot == null) return@setOnTouchListener true

            if (event.action == MotionEvent.ACTION_UP) {
                val hit = hitTest(event.x.toInt(), event.y.toInt())
                if (hit != null) showContextMenu(hit.landId, hit.faction)
            }
            true
        }
    }

    private suspend fun <T> pickSecondViewMenu(
        options: List<OptionUi>,
        mapper: (Long) -> T?
    ): T? {
        return uiProcess.pickMenuWindow(
            options = options,
            backgroundResId = R.drawable.dialog_background
        ) { result ->
            mapper(result.id)
        }
    }

    private fun currentMarker(
        current: Boolean,
        label: String
    ): String {
        return if (current) "$label (current)" else label
    }

    private fun hitTest(x: Int, y: Int): MapHit? {
        val snap = currentSnapshot ?: return null

        // 1) Check flags first (radius hit)
        val radius = resources.getDimensionPixelSize(R.dimen.flag_size) / 2
        snap.flags.firstOrNull { f ->
            val dx = f.screenX - x
            val dy = f.screenY - y
            dx * dx + dy * dy <= radius * radius
        }?.let { f ->
            return MapHit(f.landId, f.faction)
        }

        // 2) Otherwise: nearest settlement/land center
        snap.settlements.firstOrNull { s ->
            val dx = s.screenX - x
            val dy = s.screenY - y
            dx * dx + dy * dy <= 40 * 40
        }?.let { s ->
            return MapHit(s.landId, null)
        }

        return null
    }

    private fun loadAndRender() {
        lifecycleScope.launch {
            val w = overlay.width
            val h = overlay.height
            if (w <= 0 || h <= 0) return@launch

            overlay.isEnabled = false

            val snapshot = controller.getGlobalMapSnapshot(w, h)
            currentSnapshot = snapshot
            overlay.removeAllViews()
            // settlements (optional pins)
//            snapshot.settlements.forEach { s -> addPin(s.screenX, s.screenY, R.drawable.pin_settlement) }
            // flags
            snapshot.flags.forEach { f ->
                addFlag(f)
//                f.label?.let { addLabel(f.screenX, f.screenY, it) }
            }

            overlay.isEnabled = true
        }
    }

    private fun addFlag(f: FlagMarker) {

        val imageRes = when (f.kind) {
            FlagKind.PLAYER -> R.drawable.flag_player
            else -> FactionDrawables.flagFor(f.faction)
        }

        val iv = ImageView(this).apply {
            setImageResource(imageRes)
            contentDescription = "flag_${f.key}"
        }
        val size = resources.getDimensionPixelSize(R.dimen.flag_size)
        val lp = FrameLayout.LayoutParams(size, size).apply {
            leftMargin = f.screenX - size / 2
            topMargin = f.screenY - size / 2
        }
        overlay.addView(iv, lp)
    }

    private suspend fun showProcessMessage(
        title: String,
        message: String,
        positiveText: String = "OK"
    ) {
        uiProcess.pickMenuWindow(
            options = listOf(OptionUi(1L, "$positiveText\n\n$message")),
            title = title,
            backgroundResId = R.drawable.dialog_background
        ) { null }
    }

    private suspend fun showProcessConfirm(
        title: String,
        message: String,
        positiveText: String,
        negativeText: String
    ): Boolean {
        return uiProcess.pickMenuWindow(
            options = listOf(
                OptionUi(1L, "$positiveText\n\n$message"),
                OptionUi(2L, negativeText)
            ),
            title = title,
            backgroundResId = R.drawable.dialog_background
        ) { result ->
            result.id == 1L
        } ?: false
    }

    private fun buildQuantitySliderRow(
        title: String,
        minValue: Int,
        maxValue: Int,
        initialValue: Int,
        onChanged: (Int) -> Unit
    ): android.widget.LinearLayout {
        var value = initialValue.coerceIn(minValue, maxValue)

        val root = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(0, 8, 0, 16)
        }

        val label = android.widget.TextView(this).apply {
            textSize = 18f
            text = "$title: $value"
        }

        val row = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val minus = android.widget.Button(this).apply { text = "-" }
        val slider = android.widget.SeekBar(this).apply {
            max = (maxValue - minValue).coerceAtLeast(0)
            progress = value - minValue
            layoutParams = android.widget.LinearLayout.LayoutParams(
                0,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }
        val plus = android.widget.Button(this).apply { text = "+" }

        fun setValue(newValue: Int) {
            value = newValue.coerceIn(minValue, maxValue)
            slider.progress = value - minValue
            label.text = "$title: $value"
            onChanged(value)
        }

        minus.setOnClickListener { setValue(value - 1) }
        plus.setOnClickListener { setValue(value + 1) }

        slider.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: android.widget.SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    value = minValue + progress
                    label.text = "$title: $value"
                    onChanged(value)
                }
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) = Unit
        })

        row.addView(minus)
        row.addView(slider)
        row.addView(plus)

        root.addView(label)
        root.addView(row)

        return root
    }

    private fun showContextMenu(
        landId: Long,
        faction: FactionId?
    ) {
        lifecycleScope.launch {
            val items = controller.onMapItemPressed(landId, faction)

            val options = items.mapIndexed { index, item ->
                OptionUi(
                    id = index.toLong(),
                    title = item.title
                )
            } + OptionUi(-1L, getString(R.string.close))

            val selectedAction = uiProcess.pickMenuWindow(
                options = options,
                title = "",
                backgroundResId = R.drawable.dialog_background
            ) { result ->
                when {
                    result.id == -1L -> null
                    result.id.toInt() in items.indices -> items[result.id.toInt()].action
                    else -> null
                }
            } ?: return@launch

            handleMenuChoice(
                landId = landId,
                faction = faction,
                action = selectedAction
            )
        }
    }

    private fun handleMenuChoice(landId: Long, faction: FactionId?, action: MapMenuAction) {
        lifecycleScope.launch {
            when (action) {
                MapMenuAction.REPORT -> {
                    val state = controller.onChooseReport(
                        landId = landId,
                        autoconfirm = false
                    )

                    val refreshRequested = showLandReportDialog(
                        report = state.report,
                        canAskFresh = true
                    )

                    if (refreshRequested) {
                        if (state.canRefreshLocally) {
                            val refreshed = controller.onRefreshReportLocally(landId)
                            showLandReportDialog(
                                report = refreshed,
                                canAskFresh = false
                            )
                        } else {
                            val requestMode = askFreshInfoRequestMode(
                                title = "Ask chancellor for fresher land report"
                            ) ?: return@launch

                            controller.onRequestFreshReport(
                                landId = landId,
                                request = requestMode
                            )
                        }
                    }
                }

                MapMenuAction.LIST_ARMIES -> {
                    val state = controller.onChooseListArmies(
                        landId = landId,
                        autoconfirm = false
                    )

                    val refreshRequested = showArmyListDialog(
                        armies = state.armies,
                        canAskFresh = true
                    )

                    if (refreshRequested) {
                        if (state.canRefreshLocally) {
                            val refreshed = controller.onRefreshArmyListLocally(landId)
                            showArmyListDialog(
                                armies = refreshed,
                                canAskFresh = false
                            )
                        } else {
                            val requestMode = askFreshInfoRequestMode(
                                title = "Ask defense commander for fresher army list"
                            ) ?: return@launch

                            controller.onRequestFreshArmyList(
                                landId = landId,
                                request = requestMode
                            )
                        }
                    }
                }

                MapMenuAction.MOVE_ARMY_HERE -> {
                    val armyId = askArmyFromList(landId) ?: return@launch
                    val via = askDelivery() ?: return@launch
                    controller.onChooseMoveArmyHere(armyId, landId, via)
                }

                MapMenuAction.MOVE_PLAYER_HERE -> controller.onChooseMovePlayerHere(landId)

                MapMenuAction.IMPROVE_DEFENSE -> {
                    val structureType = askDefenseStructureType() ?: return@launch
                    controller.onChooseImproveDefense(
                        landId = landId,
                        structureType = structureType,
                        via = askDeliveryOrLocal(landId)
                    )
                }

                MapMenuAction.ORGANIZE_BALL -> {
                    val existing = controller.onGetActiveBallPlanForLand(landId)
                    if (existing != null) {
                        val actionChoice = askBallPlanAction(existing)
                        when (actionChoice) {
                            BallPlanUiAction.VIEW -> showBallPreparationStateDialog(existing)
                            BallPlanUiAction.CONTINUE -> openBallComposer(landId, existing.ballId)
                            BallPlanUiAction.CANCEL -> controller.onCancelBallPlan(existing.ballId)
                            BallPlanUiAction.FINALIZE -> {
                                showBallPreparationActionsDialog(
                                    state = existing,
                                    landId = landId,
                                    ballId = existing.ballId
                                )
                            }
                            null -> Unit
                        }
                    } else {
                        openBallComposer(landId, existingBallId = null)
                    }
                }

                MapMenuAction.ORGANIZE_HUNT -> controller.onChooseOrganizeHunt(
                    landId,
                    via = askDeliveryOrLocal(landId)
                )

                MapMenuAction.ORGANIZE_FESTIVAL -> controller.onChooseOrganizeFestival(
                    landId,
                    via = askDeliveryOrLocal(landId),
                    religious = false
                )

                MapMenuAction.ORGANIZE_RELIGIOUS_FESTIVAL -> controller.onChooseOrganizeFestival(
                    landId,
                    via = askDeliveryOrLocal(landId),
                    religious = true
                )

                MapMenuAction.LIST_ACTORS -> {
                    val state = controller.onChooseListActors(
                        landId,
                        autoconfirm = false
                    )

                    val refreshRequested = showActorListDialog(state.actors, state.canRefreshLocally)

                    if (refreshRequested) {
                        if (state.canRefreshLocally) {
                            val refreshed = controller.onRefreshActorListLocally(landId)
                            showActorListDialog(refreshed, canAskFresh = false)
                        } else {
                            val requestMode = askFreshInfoRequestMode(
                                title = "Ask chancellor for fresher actor list"
                            ) ?: return@launch

                            controller.onRequestFreshActorList(
                                landId = landId,
                                request = requestMode
                            )
                        }
                        return@launch
                    }

                    val chosen = askActorToMessage(state.actors) ?: return@launch
                    val orderKind = askActorOrderKind() ?: return@launch
                    val messageType = askMessageType() ?: return@launch
                    val via = askDeliveryOrLocal(landId)

                    val draft = buildActorMessageDraft(
                        actor = chosen,
                        landId = landId,
                        messageType = messageType,
                        orderKind = orderKind
                    ) ?: return@launch

                    controller.onSendStructuredOrder(
                        toRole = null,
                        toActorId = chosen.actorId,
                        draft = draft,
                        delivery = via,
                        sealed = (messageType == MessageType.SEALED_LETTER)
                    )
                }

                MapMenuAction.ORGANIZE_REBELLION_HERE -> controller.onCommandOrganizeRebellion(
                    landId,
                    via = askDeliveryOrLocal(landId)
                )

                MapMenuAction.CLOSE -> Unit
            }
        }
    }

    private suspend fun askBallPlanAction(
        state: BallPreparationState
    ): BallPlanUiAction? {
        val options = buildList {
            add(OptionUi(1L, "Continue preparation"))
            add(OptionUi(2L, "View preparation state"))
            if (state.ready) {
                add(OptionUi(3L, "Finalize ball"))
            }
            add(OptionUi(4L, "Cancel preparation"))
            add(OptionUi(-1L, "Close"))
        }

        return pickSecondViewMenu(options) { id ->
            when (id) {
                1L -> BallPlanUiAction.CONTINUE
                2L -> BallPlanUiAction.VIEW
                3L -> BallPlanUiAction.FINALIZE
                4L -> BallPlanUiAction.CANCEL
                else -> null
            }
        }
    }

    private suspend fun buildActorMessageDraft(
        actor: ActorView,
        landId: Long,
        messageType: MessageType,
        orderKind: OrderKind
    ): MessageDraft? {
        return when (orderKind) {
            OrderKind.INVITE_TO_BALL,
            OrderKind.INVITE_TO_FESTIVAL,
            OrderKind.INVITE_TO_RELIGIOUS_FESTIVAL,
            OrderKind.INVITE_TO_HUNT,
            OrderKind.CALL_FOR_DUEL,
            OrderKind.JOIN_RETINUE,
            OrderKind.COLLECT_INFO_ABOUT_ACTORS,
            OrderKind.ORDER_KILL_ACTORS,
            OrderKind.CHECK_LOYALTY_ACTORS,
            OrderKind.IMPRISON_ACTORS,
            OrderKind.RELEASE_ACTORS,
            OrderKind.COLLECT_TAX_FROM_ACTORS -> {
                val note = askOptionalNote("Additional note (optional)")
                MessageDraft(
                    messageType = messageType,
                    orderKind = orderKind,
                    actorIds = listOf(actor.actorId),
                    targetActorId = actor.actorId,
                    targetLandId = landId,
                    freeTextNote = note
                )
            }

            OrderKind.PROPOSE_MARRIAGE -> {
                val secondActorId = askActorIdInput("Enter second actor ID for marriage proposal")
                    ?: return null

                MessageDraft(
                    messageType = messageType,
                    orderKind = orderKind,
                    actorIds = listOf(actor.actorId, secondActorId),
                    marriageProposal = MarriageProposal(
                        actorAId = actor.actorId,
                        actorBId = secondActorId
                    )
                )
            }

            OrderKind.PROPOSE_COUNCIL_ROLE -> {
                val role = askCouncilRole() ?: return null

                MessageDraft(
                    messageType = messageType,
                    orderKind = orderKind,
                    actorIds = listOf(actor.actorId),
                    councilRoleProposal = CouncilRoleProposalTarget(
                        role = role,
                        actorId = actor.actorId
                    )
                )
            }

            else -> null
        }
    }

    private suspend fun askDeliveryMethodChoiceWithSendLater(
        current: DeliveryMethod,
        allowPersonalVisit: Boolean,
        senderLandId: Long? = null,
        selectedMessengers: Int = 0,
        selectedDoves: Int = 0
    ): BallDeliveryChoice? {
        val carrierCounts = senderLandId?.let {
            controller.onGetAvailableDeliveryCarriersForLand(it)
        }

        val availableMessengers = carrierCounts?.first
        val availableDoves = carrierCounts?.second

        fun messengerLabel(): String {
            return if (availableMessengers == null) {
                "Via messenger"
            } else {
                "Via messenger ($selectedMessengers used / $availableMessengers available)"
            }
        }

        fun doveLabel(): String {
            return if (availableDoves == null) {
                "Via dove"
            } else {
                "Via dove ($selectedDoves used / $availableDoves available)"
            }
        }

        val options = buildList {
            if (allowPersonalVisit) {
                add(OptionUi(1L, currentMarker(current == DeliveryMethod.LOCAL, "Deliver personally")))
            }
            add(OptionUi(2L, currentMarker(current == DeliveryMethod.MESSENGER, messengerLabel())))
            add(OptionUi(3L, currentMarker(current == DeliveryMethod.DOVE, doveLabel())))
            add(OptionUi(4L, "Send later"))
            add(OptionUi(-1L, "Cancel"))
        }

        return pickSecondViewMenu(options) { id ->
            when (id) {
                1L -> BallDeliveryChoice.PERSONAL_VISIT
                2L -> BallDeliveryChoice.MESSENGER
                3L -> BallDeliveryChoice.DOVE
                4L -> BallDeliveryChoice.SEND_LATER
                else -> null
            }
        }
    }

    private fun showBallPreparationActionsDialog(
        state: BallPreparationState,
        landId: Long,
        ballId: Long
    ) {
        lifecycleScope.launch {
            val draft = controller.onGetBallPlanDraft(ballId)
            val locationIssues = draft?.let { ballBlockingLocationIssues(it) }
                ?: listOf("The ball draft was not found.")

            val missingText = buildList {
                if (state.missingItems.isEmpty()) {
                    add("Nothing is missing.")
                } else {
                    addAll(state.missingItems)
                }
                addAll(locationIssues)
            }.distinct().joinToString("\n")

            val canConfirm = state.ready && locationIssues.isEmpty()

            val dialogMessage = buildString {
                appendLine("Turn: ${state.eventTurn}")
                appendLine("Ready: ${if (canConfirm) "Yes" else "No"}")
                appendLine()
                appendLine("Missing / warnings:")
                append(missingText)
            }

            val content = android.widget.TextView(this@SecondView).apply {
                text = dialogMessage
                textSize = 18f
                setPadding(36, 24, 36, 24)
            }

            fun buildBallSendResultMessage(result: GameEngine.BallMessageSendResult.Success): String {
                return buildString {
                    appendLine("Messages sent: ${result.sentCount}.")

                    if (result.sentMessages.isNotEmpty()) {
                        appendLine()
                        appendLine("Sent:")
                        appendLine(result.sentMessages.joinToString("\n"))
                    }

                    if (result.skippedMessages.isNotEmpty()) {
                        appendLine()
                        appendLine("Not sent:")
                        result.skippedMessages.forEachIndexed { index, skipped ->
                            appendLine("- ${skipped.ifBlank { "Skipped message #${index + 1}: no reason was provided by sendBallMessagesNow()." }}")
                        }
                    }
                }.trim()
            }

            uiProcess.showContentWindow(
                title = "Ball preparation",
                contentView = content,
                positiveText = if (canConfirm) "Confirm" else "",
                negativeText = "Close",
                neutralText = "Back to edit",
                extraText = "Send messages now",
                backgroundResId = R.drawable.dialog_background,
                onExtra = { dialog ->
                    lifecycleScope.launch {
                        val currentDraft = controller.onGetBallPlanDraft(ballId)
                        if (currentDraft == null) {
                            showProcessWarning(
                                title = "Cannot send messages",
                                message = "The ball draft was not found.",
                                positiveText = "Back to edit",
                                onPositive = {
                                    openBallComposer(
                                        landId = landId,
                                        existingBallId = ballId
                                    )
                                }
                            )
                            return@launch
                        }

                        val validation = controller.onValidateBallDeliveryCapacity(currentDraft)
                        if (!validation.ok) {
                            showProcessWarning(
                                title = "Cannot send messages",
                                message = validation.message
                                    ?: "Message delivery capacity is insufficient."
                            )
                            return@launch
                        }

                        val result = controller.onSendBallMessagesNow(ballId)

                        dialog.dismiss()

                        when (result) {
                            is GameEngine.BallMessageSendResult.Success -> {
                                showProcessWarning(
                                    title = "Messages sent",
                                    message = buildBallSendResultMessage(result),
                                    positiveText = "Back to edit",
                                    onPositive = {
                                        openBallComposer(
                                            landId = landId,
                                            existingBallId = ballId
                                        )
                                    }
                                )
                            }

                            is GameEngine.BallMessageSendResult.Blocked -> {
                                showProcessWarning(
                                    title = "Cannot send messages",
                                    message = result.message,
                                    positiveText = "Back to edit",
                                    onPositive = {
                                        openBallComposer(
                                            landId = landId,
                                            existingBallId = ballId
                                        )
                                    }
                                )
                            }
                        }
                    }
                },
                onPositive = { dialog ->
                    lifecycleScope.launch {
                        val currentDraft = controller.onGetBallPlanDraft(ballId)
                        if (currentDraft == null) {
                            dialog.dismiss()
                            showProcessWarning(
                                title = "Cannot continue",
                                message = "The ball draft was not found.",
                                positiveText = "Back to edit",
                                onPositive = {
                                    openBallComposer(
                                        landId = landId,
                                        existingBallId = ballId
                                    )
                                }
                            )
                            return@launch
                        }

                        val confirmIssues = ballBlockingLocationIssues(currentDraft)
                        if (confirmIssues.isNotEmpty()) {
                            dialog.dismiss()
                            showProcessWarning(
                                title = "Cannot release ball",
                                message = confirmIssues.joinToString("\n"),
                                positiveText = "Back to edit",
                                onPositive = {
                                    openBallComposer(
                                        landId = landId,
                                        existingBallId = ballId
                                    )
                                }
                            )
                            return@launch
                        }

                        val delegatedToChancellor =
                            currentDraft.organizerActorId != currentDraft.orderedByActorId

                        val deliveryValidation = controller.onValidateBallDeliveryCapacity(currentDraft)

                        if (!deliveryValidation.ok) {
                            dialog.dismiss()
                            showProcessWarning(
                                title = if (delegatedToChancellor) {
                                    "Cannot send order to chancellor"
                                } else {
                                    "Cannot release ball"
                                },
                                message = deliveryValidation.message
                                    ?: "Message delivery capacity is insufficient.",
                                positiveText = "Back to edit",
                                onPositive = {
                                    openBallComposer(
                                        landId = landId,
                                        existingBallId = ballId
                                    )
                                }
                            )
                            return@launch
                        }

                        if (!state.ready) {
                            dialog.dismiss()
                            showProcessWarning(
                                title = if (delegatedToChancellor) {
                                    "Cannot send order to chancellor"
                                } else {
                                    "Cannot release ball"
                                },
                                message = if (state.missingItems.isEmpty()) {
                                    "The ball is not ready."
                                } else {
                                    state.missingItems.joinToString("\n")
                                },
                                positiveText = "Back to edit",
                                onPositive = {
                                    openBallComposer(
                                        landId = landId,
                                        existingBallId = ballId
                                    )
                                }
                            )
                            return@launch
                        }

                        val result = controller.onFinalizeBallPlan(ballId)
                        dialog.dismiss()

                        showBallFinalizeResultDialog(
                            result = result,
                            delegatedToChancellor = delegatedToChancellor,
                            landId = landId,
                            ballId = ballId
                        )
                    }
                },
                onNegative = { dialog ->
                    dialog.dismiss()
                },
                onNeutral = { dialog ->
                    dialog.dismiss()
                    openBallComposer(landId = landId, existingBallId = ballId)
                }
            )
        }
    }

    private suspend fun askLocalMusicianPlan(
        currentPlans: List<BardPlanItem>,
        allowPersonalVisit: Boolean,
        senderLandId: Long
    ): List<BardPlanItem>? = suspendCancellableCoroutine { cont ->
        val existingLocalPlans = currentPlans.filter { it.generatedLocally }
        val initialCount = existingLocalPlans.size.coerceAtLeast(1)

        val firstExisting = existingLocalPlans.firstOrNull()
        var selectedCount = initialCount
        var selectedDelivery = firstExisting?.deliveryMethod ?: DeliveryMethod.MESSENGER
        var selectedSendLater = firstExisting?.sendLater ?: false
        val selectedFee = controller.onGetDefaultBardFeeForNotability(1)

        if (selectedDelivery == DeliveryMethod.LOCAL && !allowPersonalVisit) {
            selectedDelivery = DeliveryMethod.MESSENGER
            selectedSendLater = false
        }

        val scrollView = android.widget.ScrollView(this).apply { isFillViewport = true }

        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(36, 24, 36, 24)
        }

        scrollView.addView(container)

        container.addView(
            android.widget.TextView(this).apply {
                textSize = 16f
                text = "This sends an order to the mayor of the ball land to provide local musicians."
            }
        )

        fun deliverySummary(): String {
            return when {
                selectedSendLater -> "Send later"
                selectedDelivery == DeliveryMethod.LOCAL -> "Deliver personally"
                selectedDelivery == DeliveryMethod.MESSENGER -> "Via messenger"
                selectedDelivery == DeliveryMethod.DOVE -> "Via dove"
                else -> "Via messenger"
            }
        }

        fun selectedMessengerCount(): Int {
            return if (!selectedSendLater && selectedDelivery == DeliveryMethod.MESSENGER) 1 else 0
        }

        fun selectedDoveCount(): Int {
            return if (!selectedSendLater && selectedDelivery == DeliveryMethod.DOVE) 1 else 0
        }

        val countSlider = buildQuantitySliderRow(
            title = "Number of local musicians requested from mayor",
            minValue = 1,
            maxValue = 50,
            initialValue = selectedCount
        ) { selectedCount = it }

        val deliveryLabel = android.widget.TextView(this).apply {
            textSize = 18f
            text = "Delivery of mayor order: ${deliverySummary()}"
            setPadding(0, 24, 0, 0)
        }

        val messageTypeLabel = android.widget.TextView(this).apply {
            textSize = 18f
            text = "Message type: Sealed letter"
            setPadding(0, 24, 0, 0)
        }

        val feeLabel = android.widget.TextView(this).apply {
            textSize = 18f
            text = "Standard fee per local musician: $selectedFee"
            setPadding(0, 24, 0, 0)
        }

        container.addView(countSlider)
        container.addView(deliveryLabel)
        container.addView(messageTypeLabel)
        container.addView(feeLabel)

        deliveryLabel.setOnClickListener {
            lifecycleScope.launch {
                val choice = askDeliveryMethodChoiceWithSendLater(
                    current = selectedDelivery,
                    allowPersonalVisit = allowPersonalVisit,
                    senderLandId = senderLandId,
                    selectedMessengers = selectedMessengerCount(),
                    selectedDoves = selectedDoveCount()
                ) ?: return@launch

                when (choice) {
                    BallDeliveryChoice.SEND_LATER -> selectedSendLater = true
                    BallDeliveryChoice.PERSONAL_VISIT -> {
                        if (allowPersonalVisit) {
                            selectedSendLater = false
                            selectedDelivery = DeliveryMethod.LOCAL
                        }
                    }
                    BallDeliveryChoice.MESSENGER -> {
                        selectedSendLater = false
                        selectedDelivery = DeliveryMethod.MESSENGER
                    }
                    BallDeliveryChoice.DOVE -> {
                        selectedSendLater = false
                        selectedDelivery = DeliveryMethod.DOVE
                    }
                }

                deliveryLabel.text = "Delivery of mayor order: ${deliverySummary()}"
            }
        }

        uiProcess.showContentWindow(
            title = "Local musicians",
            contentView = scrollView,
            positiveText = "OK",
            negativeText = "Cancel",
            backgroundResId = R.drawable.dialog_background,
            onPositive = { dialog ->
                val rebuilt = MutableList(selectedCount) { index ->
                    val existing = existingLocalPlans.getOrNull(index)
                    BardPlanItem(
                        actorId = existing?.actorId ?: -1L,
                        deliveryMethod = if (selectedSendLater) existing?.deliveryMethod ?: selectedDelivery else selectedDelivery,
                        sealedLetter = true,
                        oral = false,
                        feeSilver = selectedFee,
                        notabilityLevel = 1,
                        generatedLocally = true,
                        sendLater = selectedSendLater,
                        invitationSent = existing?.invitationSent ?: false
                    )
                }
                cont.resume(rebuilt) {}
                dialog.dismiss()
            },
            onNegative = { dialog ->
                cont.resume(null) {}
                dialog.dismiss()
            }
        )
    }

    private fun localMusicianDeliverySummary(
        deliveryMethod: DeliveryMethod,
        sendLater: Boolean
    ): String {
        return when {
            sendLater -> "Send later"
            deliveryMethod == DeliveryMethod.LOCAL -> "Deliver personally"
            deliveryMethod == DeliveryMethod.MESSENGER -> "Via messenger"
            deliveryMethod == DeliveryMethod.DOVE -> "Via dove"
            else -> "Deliver personally"
        }
    }

    private fun localMusicianMessageTypeSummary(
        oral: Boolean,
        sealedLetter: Boolean
    ): String {
        return "Sealed letter"
    }

    private suspend fun askInvitationMessageType(
        currentOral: Boolean,
        currentSealedLetter: Boolean
    ): Pair<Boolean, Boolean>? {
        val options = listOf(
            OptionUi(1L, "Sealed letter"),
            OptionUi(-1L, "Cancel")
        )

        return pickSecondViewMenu(options) { id ->
            when (id) {
                1L -> false to true
                else -> null
            }
        }
    }

    private suspend fun askBallScale(
        currentScale: String?
    ): String? {
        val normalized = currentScale?.trim()?.uppercase()

        val options = listOf(
            OptionUi(1L, currentMarker(normalized == "LITTLE", "Little")),
            OptionUi(2L, currentMarker(normalized == "MIDDLE", "Middle")),
            OptionUi(3L, currentMarker(normalized == "BIG", "Big")),
            OptionUi(-1L, "Cancel")
        )

        return pickSecondViewMenu(options) { id ->
            when (id) {
                1L -> "LITTLE"
                2L -> "MIDDLE"
                3L -> "BIG"
                else -> null
            }
        }
    }

    private suspend fun ballBlockingLocationIssues(
        draft: BallPlanDraft
    ): List<String> {
        val issues = mutableListOf<String>()

        val delegatedToChancellor = draft.organizerActorId != draft.orderedByActorId

        if (delegatedToChancellor) {
            val chancellorLandId = controller.onGetKnownActorLand(
                viewerActorId = draft.orderedByActorId,
                actorId = draft.organizerActorId
            )

            if (chancellorLandId == null) {
                issues += "The chancellor's location is unknown."
            }
        }

        if (draft.budgetUsesCoinHolder) {
            val coinHolderActorId = controller.onGetCurrentCoinHolderActorId(
                viewerActorId = draft.orderedByActorId
            )

            if (coinHolderActorId == null) {
                issues += "No coin holder is known to the requestor."
            } else {
                val coinHolderLandId = controller.onGetKnownActorLand(
                    viewerActorId = draft.orderedByActorId,
                    actorId = coinHolderActorId
                )

                if (coinHolderLandId == null) {
                    issues += "The coin holder's location is unknown."
                }
            }
        }

        return issues
    }

    private fun openBallComposer(landId: Long, existingBallId: Long?) {
        lifecycleScope.launch {
            val currentActorId = controller.onGetCurrentActorId()
            val existingDraft = existingBallId?.let { controller.onGetBallPlanDraft(it) }
            val titleOptions = controller.onGetBallTitleRankOptions()

            val orderedByActorIdForPrecheck = existingDraft?.orderedByActorId ?: currentActorId
            val hasVenueForOrderGiver = controller.onCanOrganizeBallInLand(
                landId = landId,
                orderedByActorId = orderedByActorIdForPrecheck
            )

            if (!hasVenueForOrderGiver) {
                showProcessWarning(
                    title = "Cannot organize ball",
                    message = "The person who ordered the ball has no manor or palace in this land."
                )
                return@launch
            }

            var orderedByActorId = existingDraft?.orderedByActorId ?: currentActorId

            var resolvedChancellorActorId = controller.onGetCurrentChancellorActorId()
            val initialResolvedChancellorId =
                if (resolvedChancellorActorId != null && resolvedChancellorActorId > 0L) {
                    resolvedChancellorActorId
                } else {
                    null
                }

            val hasResolvableChancellor = initialResolvedChancellorId != null

            var organizerActorId = when {
                existingDraft == null && initialResolvedChancellorId != null ->
                    initialResolvedChancellorId
                existingDraft == null ->
                    currentActorId
                existingDraft.organizerActorId == orderedByActorId ->
                    orderedByActorId
                initialResolvedChancellorId != null &&
                        existingDraft.organizerActorId == initialResolvedChancellorId ->
                    initialResolvedChancellorId
                else ->
                    orderedByActorId
            }

            var delegatedToChancellor = when {
                existingDraft == null -> hasResolvableChancellor
                else -> hasResolvableChancellor && organizerActorId != orderedByActorId
            }

            var options = controller.onGetBallPlanningOptions(
                landId = landId,
                orderedByActorId = orderedByActorId,
                organizerActorId = organizerActorId
            )

            val root = android.widget.ScrollView(this@SecondView)
            val content = android.widget.LinearLayout(this@SecondView).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(36, 24, 36, 24)
            }
            root.addView(content)

            suspend fun refreshPlanningOptions() {
                options = controller.onGetBallPlanningOptions(
                    landId = landId,
                    orderedByActorId = orderedByActorId,
                    organizerActorId = organizerActorId
                )
            }

            suspend fun askOrganizerMode(
                currentDelegated: Boolean,
                chancellorAvailable: Boolean
            ): Boolean? {
                val entries = buildList {
                    add(OptionUi(1L, currentMarker(!currentDelegated, "Organize personally")))
                    if (chancellorAvailable) {
                        add(OptionUi(2L, currentMarker(currentDelegated, "Delegate to chancellor")))
                    }
                    add(OptionUi(-1L, "Cancel"))
                }

                return pickSecondViewMenu(entries) { id ->
                    when (id) {
                        1L -> false
                        2L -> true
                        else -> null
                    }
                }
            }

            fun normalizedScaleOrDefault(scale: String?): String {
                return when (scale?.trim()?.uppercase()) {
                    "LITTLE" -> "LITTLE"
                    "BIG" -> "BIG"
                    else -> "MIDDLE"
                }
            }

            data class PersonalScaleRecommendation(
                val bardText: String,
                val inviteeText: String,
                val wineText: String
            )

            fun recommendationForScale(scale: String): PersonalScaleRecommendation {
                return when (normalizedScaleOrDefault(scale)) {
                    "LITTLE" -> PersonalScaleRecommendation(
                        bardText = "1",
                        inviteeText = "1-20",
                        wineText = "1-14"
                    )
                    "BIG" -> PersonalScaleRecommendation(
                        bardText = "4-5",
                        inviteeText = "61+",
                        wineText = "43-70"
                    )
                    else -> PersonalScaleRecommendation(
                        bardText = "2-3",
                        inviteeText = "21-60",
                        wineText = "15-42"
                    )
                }
            }

            var selectedTurn = existingDraft?.eventTurn ?: options.possibleTurns.firstOrNull() ?: 0
            var selectedScale = normalizedScaleOrDefault(existingDraft?.scale)
            var delegatedUseAlcohol = existingDraft?.useAlcohol ?: false

            val chosenBards = mutableListOf<BardPlanItem>().apply {
                addAll(existingDraft?.bardPlans ?: emptyList())

                if (!delegatedToChancellor) {
                    val savedLocalCount = existingDraft?.localMusicianCount ?: 0
                    val alreadyRestoredLocalCount = count { it.generatedLocally }
                    val missingLocalCount = (savedLocalCount - alreadyRestoredLocalCount).coerceAtLeast(0)

                    repeat(missingLocalCount) {
                        add(
                            BardPlanItem(
                                actorId = -1L,
                                deliveryMethod = DeliveryMethod.MESSENGER,
                                sealedLetter = true,
                                oral = false,
                                feeSilver = controller.onGetDefaultBardFeeForNotability(1),
                                notabilityLevel = 1,
                                generatedLocally = true,
                                sendLater = false
                            )
                        )
                    }
                }
            }

            val chosenGuests = mutableListOf<GuestPlanItem>().apply {
                addAll(existingDraft?.attendeePlans ?: emptyList())
            }

            var budgetPlan = existingDraft?.budgetFromStocks?.toMutableList() ?: mutableListOf()

            var budgetUsesCoinHolder = existingDraft?.budgetUsesCoinHolder ?: false

            if (budgetUsesCoinHolder) {
                budgetPlan.clear()
            }

            var selectedCoinHolderOrderDeliveryMethod =
                existingDraft?.coinHolderOrderDeliveryMethod ?: DeliveryMethod.MESSENGER
            var selectedCoinHolderOrderSendLater =
                existingDraft?.coinHolderOrderSendLater ?: false
            var selectedDelegationOrderDeliveryMethod =
                existingDraft?.delegationOrderDeliveryMethod ?: DeliveryMethod.MESSENGER
            var selectedDelegationOrderSendLater =
                existingDraft?.delegationOrderSendLater ?: false

            val cachedAttendeeNames = linkedMapOf<Long, String>()
            val cachedBardNames = linkedMapOf<Long, String>()

            fun refreshNameCaches() {
                options.attendees.forEach { cachedAttendeeNames[it.actorId] = it.name }
                options.bards.forEach { cachedBardNames[it.actorId] = it.name }
                cachedBardNames[-1L] = "Local musicians"
            }

            suspend fun pruneSelectedActorsThatCannotBeTargeted() {
                val knowledgeActorId = if (delegatedToChancellor) {
                    orderedByActorId
                } else {
                    organizerActorId
                }

                val knownGuestIdsFromOptions = options.attendees.map { it.actorId }.toSet()
                val knownBardIdsFromOptions = options.bards.map { it.actorId }.toSet()

                val guestsToRemove = chosenGuests.filter { guest ->
                    guest.actorId !in knownGuestIdsFromOptions ||
                            controller.onGetKnownActorLand(
                                viewerActorId = knowledgeActorId,
                                actorId = guest.actorId
                            ) == null
                }.map { it.actorId }.toSet()

                val bardsToRemove = chosenBards.filter { bard ->
                    !bard.generatedLocally &&
                            (
                                    bard.actorId !in knownBardIdsFromOptions ||
                                            controller.onGetKnownActorLand(
                                                viewerActorId = knowledgeActorId,
                                                actorId = bard.actorId
                                            ) == null
                                    )
                }.map { it.actorId }.toSet()

                chosenGuests.removeAll { it.actorId in guestsToRemove }
                chosenBards.removeAll { it.actorId in bardsToRemove }

                if (delegatedToChancellor) {
                    chosenBards.removeAll { it.generatedLocally }
                }

                cachedAttendeeNames.keys.removeAll { it !in knownGuestIdsFromOptions }
                cachedBardNames.keys.removeAll { it != -1L && it !in knownBardIdsFromOptions }
            }

            refreshNameCaches()

            fun attendeeNameById(): Map<Long, String> = cachedAttendeeNames.toMap()
            fun bardNameById(): Map<Long, String> = cachedBardNames.toMap()

            var chosenVenue: VenueOption? =
                options.venues.firstOrNull { it.structureId == existingDraft?.venueStructureId }
                    ?: options.venues.firstOrNull()

            var minimumTitleRank = existingDraft?.localInviteMinimumTitleRank
                ?: (titleOptions.minByOrNull { it.rank }?.rank ?: 0)

            var alcoholPlan = if (delegatedToChancellor) {
                BallAlcoholPlan()
            } else {
                existingDraft?.alcoholPlan ?: BallAlcoholPlan()
            }

            suspend fun refreshSelectedGuestNamesForSummary(
                minimumTitleRankForSummary: Int
            ) {
                val knownInviteesForSummary = (
                        controller.onGetLocalInviteeSuggestions(
                            landId = landId,
                            minimumTitleRank = minimumTitleRankForSummary,
                            organizerActorId = organizerActorId
                        ) + controller.onGetRemoteInviteeSuggestions(
                            targetLandId = landId,
                            organizerActorId = organizerActorId
                        )
                        ).distinctBy { it.actorId }

                knownInviteesForSummary.forEach { candidate ->
                    cachedAttendeeNames[candidate.actorId] = candidate.name
                }
            }

            suspend fun applyAutoInviteLocalNobles() {
                if (delegatedToChancellor) return

                val local = controller.onGetLocalInviteeSuggestions(
                    landId = landId,
                    minimumTitleRank = minimumTitleRank,
                    organizerActorId = organizerActorId
                )

                local.forEach { cachedAttendeeNames[it.actorId] = it.name }

                val existingById = chosenGuests.associateBy { it.actorId }
                val localIds = local.map { it.actorId }.toSet()

                val rebuilt = mutableListOf<GuestPlanItem>()

                local.forEach { candidate ->
                    val existingPlan = existingById[candidate.actorId]
                    rebuilt += GuestPlanItem(
                        actorId = candidate.actorId,
                        deliveryMethod = existingPlan?.deliveryMethod ?: DeliveryMethod.MESSENGER,
                        sealedLetter = existingPlan?.sealedLetter ?: true,
                        oral = existingPlan?.oral ?: false,
                        sendLater = existingPlan?.sendLater ?: false
                    )
                }

                chosenGuests
                    .filter { it.actorId !in localIds }
                    .forEach { rebuilt += it }

                chosenGuests.clear()
                chosenGuests.addAll(rebuilt)
            }

            applyAutoInviteLocalNobles()

            val organizerRow = android.widget.TextView(this@SecondView)
            val delegationDeliveryRow = android.widget.TextView(this@SecondView)
            val turnRow = android.widget.TextView(this@SecondView)
            val scaleRow = android.widget.TextView(this@SecondView)
            val deliveryCapacityRow = android.widget.TextView(this@SecondView)
            val budgetFundingModeRow = android.widget.TextView(this@SecondView)
            val coinHolderDeliveryRow = android.widget.TextView(this@SecondView)
            val budgetRow = android.widget.TextView(this@SecondView)
            val bardSummary = android.widget.TextView(this@SecondView)
            val venueRow = android.widget.TextView(this@SecondView)
            val inviteeRow = android.widget.TextView(this@SecondView)
            val wineRow = android.widget.TextView(this@SecondView)
            val minimumTitleRow = android.widget.TextView(this@SecondView)
            val baseBudgetLabel = android.widget.TextView(this@SecondView)
            val bardSubtotalLabel = android.widget.TextView(this@SecondView)
            val wineSubtotalLabel = android.widget.TextView(this@SecondView)
            val totalBudgetLabel = android.widget.TextView(this@SecondView)

            fun deliverySummary(deliveryMethod: DeliveryMethod, sendLater: Boolean = false): String {
                if (sendLater) return "Send later"
                return when (deliveryMethod) {
                    DeliveryMethod.LOCAL -> "Deliver personally"
                    DeliveryMethod.MESSENGER -> "Via messenger"
                    DeliveryMethod.DOVE -> "Via dove"
                }
            }

            fun sentStatus(sent: Boolean, delivered: Boolean = false): String {
                return when {
                    delivered -> "Delivered"
                    sent -> "Sent"
                    else -> "Not sent"
                }
            }

            suspend fun ballDeliverySenderActorId(): Long {
                return if (delegatedToChancellor) {
                    organizerActorId
                } else {
                    orderedByActorId
                }
            }

            suspend fun ballDeliverySenderCurrentLandId(): Long? {
                return controller.onGetActualActorLand(ballDeliverySenderActorId())
            }

            suspend fun budgetFundingKnowledgeActorId(): Long {
                return orderedByActorId
            }

            suspend fun knownCoinHolderActorIdForBudgetMode(): Long? {
                return controller.onGetCurrentCoinHolderActorId(
                    viewerActorId = budgetFundingKnowledgeActorId()
                )
            }

            suspend fun canPersonallyContactKnownCoinHolder(): Boolean {
                val viewerActorId = budgetFundingKnowledgeActorId()
                val coinHolderActorId = knownCoinHolderActorIdForBudgetMode() ?: return false
                val viewerCurrentLandId = controller.onGetActualActorLand(viewerActorId) ?: return false
                val knownCoinHolderLandId = controller.onGetKnownActorLand(viewerActorId, coinHolderActorId)
                return knownCoinHolderLandId != null && knownCoinHolderLandId == viewerCurrentLandId
            }

            fun updateBudgetFundingModeRow() {
                budgetFundingModeRow.text = if (budgetUsesCoinHolder) {
                    if (delegatedToChancellor) {
                        "Delegated budget funding: Ask coin holder"
                    } else {
                        "Budget funding: Ask coin holder"
                    }
                } else {
                    if (delegatedToChancellor) {
                        "Delegated budget funding: Source personally"
                    } else {
                        "Budget funding: Source personally"
                    }
                }
            }

            fun currentDraft(): BallPlanDraft {
                val localMusicians = if (delegatedToChancellor) {
                    0
                } else {
                    chosenBards.count { it.generatedLocally }
                }

                return BallPlanDraft(
                    targetLandId = landId,
                    orderedByActorId = orderedByActorId,
                    organizerActorId = organizerActorId,
                    eventTurn = selectedTurn,
                    bardPlans = chosenBards.map { bard ->
                        BardPlanItem(
                            actorId = bard.actorId,
                            deliveryMethod = bard.deliveryMethod,
                            sealedLetter = bard.sealedLetter,
                            oral = bard.oral,
                            feeSilver = bard.feeSilver,
                            notabilityLevel = bard.notabilityLevel,
                            generatedLocally = if (delegatedToChancellor) false else bard.generatedLocally,
                            sendLater = if (delegatedToChancellor) true else bard.sendLater,
                            invitationSent = bard.invitationSent
                        )
                    },
                    attendeePlans = chosenGuests.map { guest ->
                        GuestPlanItem(
                            actorId = guest.actorId,
                            deliveryMethod = guest.deliveryMethod,
                            sealedLetter = guest.sealedLetter,
                            oral = guest.oral,
                            sendLater = if (delegatedToChancellor) true else guest.sendLater,
                            invitationSent = guest.invitationSent
                        )
                    },
                    localMusicianCount = localMusicians,
                    inviteLocalPeople = false,
                    venueStructureId = chosenVenue?.structureId,
                    localInviteMinimumTitleRank = minimumTitleRank,
                    alcoholPlan = if (delegatedToChancellor) BallAlcoholPlan() else alcoholPlan,
                    budgetFromStocks = if (budgetUsesCoinHolder) emptyList() else budgetPlan.toList(),
                    coinHolderOrderDeliveryMethod = selectedCoinHolderOrderDeliveryMethod,
                    coinHolderOrderSendLater = selectedCoinHolderOrderSendLater,
                    delegationOrderDeliveryMethod = selectedDelegationOrderDeliveryMethod,
                    delegationOrderSendLater = selectedDelegationOrderSendLater,
                    scale = selectedScale,
                    useAlcohol = if (delegatedToChancellor) delegatedUseAlcohol else false,
                    budgetUsesCoinHolder = budgetUsesCoinHolder,
                    coinHolderOrderSent = existingDraft?.coinHolderOrderSent ?: false,
                    coinHolderOrderDelivered = existingDraft?.coinHolderOrderDelivered ?: false,
                    delegationOrderSent = existingDraft?.delegationOrderSent ?: false,
                    delegationOrderDelivered = existingDraft?.delegationOrderDelivered ?: false
                )
            }

            suspend fun currentDeliveryCapacity(): GameEngine.BallDeliveryCapacityResult {
                return controller.onValidateBallDeliveryCapacity(currentDraft())
            }

            suspend fun deliveryCapacityWithoutDelegationOrder(): GameEngine.BallDeliveryCapacityResult {
                return controller.onValidateBallDeliveryCapacity(
                    currentDraft().copy(
                        delegationOrderSendLater = true
                    )
                )
            }

            suspend fun deliveryCapacityWithoutCoinHolderOrder(): GameEngine.BallDeliveryCapacityResult {
                return controller.onValidateBallDeliveryCapacity(
                    currentDraft().copy(
                        coinHolderOrderSendLater = true
                    )
                )
            }

            suspend fun deliveryCapacityWithoutWineSources(): GameEngine.BallDeliveryCapacityResult {
                return controller.onValidateBallDeliveryCapacity(
                    currentDraft().copy(
                        alcoholPlan = currentDraft().alcoholPlan.copy(fromStocks = emptyList())
                    )
                )
            }

            suspend fun deliveryCapacityWithoutBudgetSources(): GameEngine.BallDeliveryCapacityResult {
                return controller.onValidateBallDeliveryCapacity(
                    currentDraft().copy(budgetFromStocks = emptyList())
                )
            }

            fun bardSummaryText(): String {
                val nameById = bardNameById()

                val localPlans = chosenBards.filter { it.generatedLocally }
                val namedPlans = chosenBards.filterNot { it.generatedLocally }

                val names = mutableListOf<String>()

                namedPlans.forEach { plan ->
                    val name = nameById[plan.actorId] ?: "Actor #${plan.actorId}"

                    names += if (delegatedToChancellor) {
                        "$name (wishlist)"
                    } else {
                        "$name (${deliverySummary(plan.deliveryMethod, plan.sendLater)}, ${sentStatus(plan.invitationSent)})"
                    }
                }

                if (!delegatedToChancellor && localPlans.isNotEmpty()) {
                    val representative = localPlans.first()
                    val count = localPlans.size

                    val deliveryText = when {
                        representative.sendLater -> "send later"
                        representative.deliveryMethod == DeliveryMethod.LOCAL -> "deliver personally"
                        representative.deliveryMethod == DeliveryMethod.MESSENGER -> "via messenger"
                        representative.deliveryMethod == DeliveryMethod.DOVE -> "via dove"
                        else -> "via messenger"
                    }

                    val statusText = sentStatus(localPlans.any { it.invitationSent })
                    names += if (count == 1) {
                        "1 local musician requested from mayor ($deliveryText, $statusText)"
                    } else {
                        "$count local musicians requested from mayor ($deliveryText, $statusText)"
                    }
                }

                if (delegatedToChancellor) {
                    return "Bards wishlist: ${names.joinToString().ifBlank { "None" }}"
                }

                val recommendation = recommendationForScale(selectedScale)
                return "Bards: ${names.joinToString().ifBlank { "None" }}\nRecommended for ${selectedScale.lowercase()}: ${recommendation.bardText}"
            }

            fun inviteeSummaryText(): String {
                val nameById = attendeeNameById()
                val names = chosenGuests.map { plan ->
                    val name = nameById[plan.actorId] ?: "Actor #${plan.actorId}"

                    if (delegatedToChancellor) {
                        "$name (wishlist)"
                    } else {
                        "$name (${deliverySummary(plan.deliveryMethod, plan.sendLater)}, ${sentStatus(plan.invitationSent)})"
                    }
                }

                if (delegatedToChancellor) {
                    return "Invitees wishlist: ${names.joinToString().ifBlank { "None" }}"
                }

                val recommendation = recommendationForScale(selectedScale)
                return "Invitees: ${names.joinToString().ifBlank { "None" }}\nRecommended for ${selectedScale.lowercase()}: ${recommendation.inviteeText}"
            }

            suspend fun wineSummaryText(): String {
                if (delegatedToChancellor) {
                    return "Alcohol: ${if (delegatedUseAlcohol) "Allowed" else "Not allowed"}"
                }

                val stockTotal = alcoholPlan.fromStocks.sumOf { it.quantity }
                val recommendation = recommendationForScale(selectedScale)

                val sourceLines = mutableListOf<String>()

                alcoholPlan.fromStocks
                    .filter { it.quantity > 0 }
                    .forEach { use ->
                        val landName = controller.onGetLandName(use.landId) ?: "Land ${use.landId}"
                        sourceLines += "- $landName: ${use.quantity} wine (${deliverySummary(use.deliveryMethod, use.sendLater)}, ${sentStatus(use.orderSent, use.orderDelivered)})"
                    }

                val sourcesText = sourceLines.joinToString("\n").ifBlank { "- None selected" }

                return "Wine from known stocks: $stockTotal\nSources:\n$sourcesText\nRecommended for ${selectedScale.lowercase()}: ${recommendation.wineText}"
            }

            suspend fun budgetSummaryText(): String {
                val parts = budgetPlan.map { use ->
                    val landName = controller.onGetLandName(use.landId) ?: "Land ${use.landId}"
                    "$landName: ${use.quantity} (${deliverySummary(use.deliveryMethod, use.sendLater)})"
                }

                return if (delegatedToChancellor) {
                    if (parts.isEmpty()) {
                        "Delegated budget sources: None selected"
                    } else {
                        "Delegated budget sources: ${parts.joinToString()}"
                    }
                } else {
                    if (parts.isEmpty()) {
                        "Budget sources: None selected"
                    } else {
                        "Budget sources: ${parts.joinToString()}"
                    }
                }
            }

            fun updateVenueRow() {
                venueRow.text = if (chosenVenue != null) {
                    "Venue: ${chosenVenue?.name ?: "Selected venue"}"
                } else {
                    "Venue: Not selected"
                }
            }

            fun updateMinimumTitleRow() {
                val titleName = if (minimumTitleRank == 0) {
                    "Any known local noble"
                } else {
                    titleOptions.firstOrNull { it.rank == minimumTitleRank }?.titleName
                        ?: "rank $minimumTitleRank"
                }
                minimumTitleRow.text = "Minimum invited title: $titleName"
            }

            fun updateScaleRow() {
                scaleRow.visibility = android.view.View.VISIBLE
                scaleRow.text = "Ball scale: ${selectedScale.lowercase().replaceFirstChar { it.uppercase() }}"
            }

            fun updateWineRow() {
                lifecycleScope.launch {
                    wineRow.text = wineSummaryText()
                }
            }

            suspend fun updateBudgetRow() {
                budgetRow.visibility = if (budgetUsesCoinHolder) {
                    android.view.View.GONE
                } else {
                    android.view.View.VISIBLE
                }

                budgetRow.text = budgetSummaryText()
            }

            fun updateCoinHolderDeliveryRow() {
                coinHolderDeliveryRow.visibility =
                    if (budgetUsesCoinHolder) android.view.View.VISIBLE else android.view.View.GONE

                val status = sentStatus(
                    sent = existingDraft?.coinHolderOrderSent ?: false,
                    delivered = existingDraft?.coinHolderOrderDelivered ?: false
                )

                coinHolderDeliveryRow.text =
                    "Coin holder delivery: ${deliverySummary(selectedCoinHolderOrderDeliveryMethod, selectedCoinHolderOrderSendLater)} ($status)"
            }

            fun updateDelegationDeliveryRow() {
                delegationDeliveryRow.visibility =
                    if (delegatedToChancellor) android.view.View.VISIBLE else android.view.View.GONE

                val status = sentStatus(
                    sent = existingDraft?.delegationOrderSent ?: false,
                    delivered = existingDraft?.delegationOrderDelivered ?: false
                )

                delegationDeliveryRow.text =
                    "Delegation delivery: ${deliverySummary(selectedDelegationOrderDeliveryMethod, selectedDelegationOrderSendLater)} ($status)"
            }

            fun updateBudgetLabels() {
                val preview = controller.onPreviewBallBudget(currentDraft())
                val delegated = currentDraft().organizerActorId != currentDraft().orderedByActorId

                if (delegated) {
                    baseBudgetLabel.visibility = android.view.View.VISIBLE
                    bardSubtotalLabel.visibility = android.view.View.GONE
                    wineSubtotalLabel.visibility = android.view.View.GONE
                    totalBudgetLabel.visibility = android.view.View.VISIBLE

                    baseBudgetLabel.text = "Expected standard budget: ${preview.baseBudget}"
                    totalBudgetLabel.text = "Expected total budget: ${preview.totalBudget}"
                } else {
                    baseBudgetLabel.visibility = android.view.View.GONE
                    bardSubtotalLabel.visibility = android.view.View.VISIBLE
                    wineSubtotalLabel.visibility = android.view.View.VISIBLE
                    totalBudgetLabel.visibility = android.view.View.VISIBLE

                    bardSubtotalLabel.text = "Bard subtotal: ${preview.bardSubtotal}"
                    wineSubtotalLabel.text = "Wine subtotal: ${preview.wineSubtotal}"
                    totalBudgetLabel.text = "Total cost: ${preview.totalBudget}"
                }
            }

            suspend fun deliveryCapacitySummaryText(): String {
                val capacity = controller.onValidateBallDeliveryCapacity(currentDraft())

                return buildString {
                    appendLine("Delivery capacity for messages selected to send now:")
                    appendLine("Messengers: ${capacity.requiredMessengers} used / ${capacity.availableMessengers} available")
                    append("Doves: ${capacity.requiredDoves} used / ${capacity.availableDoves} available")

                    if (!capacity.ok && !capacity.message.isNullOrBlank()) {
                        appendLine()
                        append(capacity.message)
                    }
                }
            }

            fun updateDeliveryCapacityRow() {
                lifecycleScope.launch {
                    deliveryCapacityRow.text = deliveryCapacitySummaryText()
                }
            }

            suspend fun validateBallOrganizerSelection(
                orderedByActorId: Long,
                organizerActorId: Long,
                targetLandId: Long
            ): List<String> {
                val issues = mutableListOf<String>()

                val canRequestorHost = controller.onCanOrganizeBallInLand(
                    landId = targetLandId,
                    orderedByActorId = orderedByActorId
                )

                if (!canRequestorHost) {
                    issues += "The person who ordered the ball has no manor or palace in this land."
                }

                if (organizerActorId != orderedByActorId) {
                    val currentChancellor = controller.onGetCurrentChancellorActorId()
                    if (currentChancellor == null || currentChancellor != organizerActorId) {
                        issues += "No chancellor is currently available for delegation."
                    }
                }

                return issues
            }

            suspend fun refreshUiAfterOrganizerChange(showProblems: Boolean) {
                refreshPlanningOptions()
                refreshNameCaches()
                pruneSelectedActorsThatCannotBeTargeted()

                if (chosenVenue?.structureId !in options.venues.map { it.structureId }.toSet()) {
                    chosenVenue = options.venues.firstOrNull()
                }

                if (!delegatedToChancellor) {
                    val normalizedBards = chosenBards.map { plan ->
                        val known = plan.generatedLocally || options.bards.any { it.actorId == plan.actorId }
                        val notability = if (plan.notabilityLevel > 0) plan.notabilityLevel else 1

                        plan.copy(
                            deliveryMethod = if (known) plan.deliveryMethod else DeliveryMethod.MESSENGER,
                            sendLater = if (known) plan.sendLater else false,
                            sealedLetter = true,
                            oral = false,
                            feeSilver = if (plan.feeSilver > 0) {
                                plan.feeSilver
                            } else {
                                controller.onGetDefaultBardFeeForNotability(notability)
                            },
                            notabilityLevel = notability
                        )
                    }

                    chosenBards.clear()
                    chosenBards.addAll(normalizedBards)

                    val normalizedGuests = chosenGuests.map { plan ->
                        val known = options.attendees.any { it.actorId == plan.actorId }
                        plan.copy(
                            deliveryMethod = if (known) plan.deliveryMethod else DeliveryMethod.MESSENGER,
                            sendLater = if (known) plan.sendLater else false,
                            sealedLetter = true,
                            oral = false
                        )
                    }

                    chosenGuests.clear()
                    chosenGuests.addAll(normalizedGuests)

                    applyAutoInviteLocalNobles()
                }

                val knownCoinHolder = knownCoinHolderActorIdForBudgetMode()
                if (budgetUsesCoinHolder && knownCoinHolder == null) {
                    budgetUsesCoinHolder = false
                }

                organizerRow.text = if (delegatedToChancellor) {
                    "Organizer: Chancellor"
                } else {
                    "Organizer: Personally"
                }

                bardSummary.text = bardSummaryText()
                inviteeRow.text = inviteeSummaryText()
                updateVenueRow()
                updateMinimumTitleRow()
                updateScaleRow()
                updateWineRow()
                updateBudgetFundingModeRow()
                updateBudgetRow()
                updateCoinHolderDeliveryRow()
                updateDelegationDeliveryRow()
                updateBudgetLabels()
                updateDeliveryCapacityRow()

                if (!showProblems) return

                val problems = validateBallOrganizerSelection(
                    orderedByActorId = orderedByActorId,
                    organizerActorId = organizerActorId,
                    targetLandId = landId
                ).toMutableList()

                if (budgetUsesCoinHolder && knownCoinHolder == null) {
                    problems += if (delegatedToChancellor) {
                        "No coin holder is known to the organizer."
                    } else {
                        "No coin holder is known to the requestor."
                    }
                }

                if (problems.isNotEmpty()) {
                    showProcessWarning(
                        title = "Ball organizer updated",
                        message = problems.joinToString("\n")
                    )
                }
            }

            fun convertDelegatedWishlistToPersonalDeliveryDefaults() {
                val convertedBards = chosenBards.map { plan ->
                    if (plan.generatedLocally || plan.invitationSent) {
                        plan
                    } else {
                        plan.copy(
                            deliveryMethod = DeliveryMethod.MESSENGER,
                            sendLater = false,
                            sealedLetter = true,
                            oral = false
                        )
                    }
                }

                chosenBards.clear()
                chosenBards.addAll(convertedBards)

                val convertedGuests = chosenGuests.map { plan ->
                    if (plan.invitationSent) {
                        plan
                    } else {
                        plan.copy(
                            deliveryMethod = DeliveryMethod.MESSENGER,
                            sendLater = false,
                            sealedLetter = true,
                            oral = false
                        )
                    }
                }

                chosenGuests.clear()
                chosenGuests.addAll(convertedGuests)
            }

            organizerRow.apply {
                textSize = 18f
                setPadding(0, 0, 0, 24)
                text = if (delegatedToChancellor) {
                    "Organizer: Chancellor"
                } else {
                    "Organizer: Personally"
                }
                setOnClickListener {
                    lifecycleScope.launch {
                        resolvedChancellorActorId = controller.onGetCurrentChancellorActorId()
                        val currentResolvedChancellorId = resolvedChancellorActorId
                        val hasChancellorNow = currentResolvedChancellorId != null && currentResolvedChancellorId > 0L

                        val wasDelegatedToChancellor = delegatedToChancellor

                        val chosenDelegation = askOrganizerMode(
                            currentDelegated = delegatedToChancellor,
                            chancellorAvailable = hasChancellorNow
                        ) ?: return@launch

                        delegatedToChancellor = chosenDelegation
                        organizerActorId = if (delegatedToChancellor) {
                            if (currentResolvedChancellorId != null && currentResolvedChancellorId > 0L) {
                                currentResolvedChancellorId
                            } else {
                                orderedByActorId
                            }
                        } else {
                            orderedByActorId
                        }

                        if (wasDelegatedToChancellor && !delegatedToChancellor) {
                            convertDelegatedWishlistToPersonalDeliveryDefaults()
                        }

                        refreshUiAfterOrganizerChange(showProblems = true)
                    }
                }
            }
            content.addView(organizerRow)

            delegationDeliveryRow.apply {
                textSize = 18f
                setPadding(0, 24, 0, 24)
                updateDelegationDeliveryRow()
                setOnClickListener {
                    lifecycleScope.launch {
                        if (!delegatedToChancellor) return@launch

                        val organizerCurrentLandId = controller.onGetActualActorLand(orderedByActorId)
                        if (organizerCurrentLandId == null) {
                            showProcessWarning(
                                title = "Cannot choose delivery",
                                message = "The requestor current location is unknown."
                            )
                            return@launch
                        }

                        val chancellorLandId =
                            controller.onGetKnownActorLand(orderedByActorId, organizerActorId)
                        val allowPersonalVisit =
                            chancellorLandId != null && chancellorLandId == organizerCurrentLandId

                        val capacity = currentDeliveryCapacity()

                        val choice = askDeliveryMethodChoiceWithSendLater(
                            current = selectedDelegationOrderDeliveryMethod,
                            allowPersonalVisit = allowPersonalVisit,
                            senderLandId = organizerCurrentLandId,
                            selectedMessengers = capacity.requiredMessengers,
                            selectedDoves = capacity.requiredDoves
                        ) ?: return@launch

                        when (choice) {
                            BallDeliveryChoice.PERSONAL_VISIT -> {
                                selectedDelegationOrderSendLater = false
                                selectedDelegationOrderDeliveryMethod =
                                    if (allowPersonalVisit) DeliveryMethod.LOCAL else DeliveryMethod.MESSENGER
                            }

                            BallDeliveryChoice.DOVE -> {
                                selectedDelegationOrderSendLater = false
                                selectedDelegationOrderDeliveryMethod = DeliveryMethod.DOVE
                            }

                            BallDeliveryChoice.MESSENGER -> {
                                selectedDelegationOrderSendLater = false
                                selectedDelegationOrderDeliveryMethod = DeliveryMethod.MESSENGER
                            }

                            BallDeliveryChoice.SEND_LATER -> {
                                selectedDelegationOrderSendLater = true
                            }
                        }

                        updateDelegationDeliveryRow()
                        updateDeliveryCapacityRow()
                    }
                }
            }
            content.addView(delegationDeliveryRow)

            deliveryCapacityRow.apply {
                textSize = 18f
                setPadding(0, 0, 0, 24)
                updateDeliveryCapacityRow()
            }
            content.addView(deliveryCapacityRow)

            turnRow.apply {
                text = "Turn: $selectedTurn"
                textSize = 18f
                setPadding(0, 0, 0, 24)
                setOnClickListener {
                    lifecycleScope.launch {
                        val picked = askTurn(options.possibleTurns, selectedTurn)
                        if (picked != null) {
                            selectedTurn = picked
                            text = "Turn: $selectedTurn"
                            updateDeliveryCapacityRow()
                        }
                    }
                }
            }
            content.addView(turnRow)

            minimumTitleRow.apply {
                textSize = 18f
                setPadding(0, 24, 0, 24)
                updateMinimumTitleRow()
                setOnClickListener {
                    lifecycleScope.launch {
                        val chosenTitleRank = askTitleRankChoice(
                            currentRank = minimumTitleRank,
                            titleOptions = titleOptions
                        )
                        if (chosenTitleRank != null) {
                            minimumTitleRank = chosenTitleRank
                            if (!delegatedToChancellor) {
                                applyAutoInviteLocalNobles()
                            }
                            updateMinimumTitleRow()
                            inviteeRow.text = inviteeSummaryText()
                            updateWineRow()
                            updateBudgetLabels()
                            updateDeliveryCapacityRow()
                        }
                    }
                }
            }
            content.addView(minimumTitleRow)

            scaleRow.apply {
                textSize = 18f
                setPadding(0, 24, 0, 24)
                updateScaleRow()
                setOnClickListener {
                    lifecycleScope.launch {
                        val chosen = askBallScale(selectedScale)
                        if (chosen != null) {
                            selectedScale = normalizedScaleOrDefault(chosen)
                            updateScaleRow()
                            bardSummary.text = bardSummaryText()
                            inviteeRow.text = inviteeSummaryText()
                            updateWineRow()
                            updateBudgetLabels()
                            updateDeliveryCapacityRow()
                        }
                    }
                }
            }
            content.addView(scaleRow)

            bardSummary.apply {
                text = bardSummaryText()
                textSize = 18f
                setPadding(0, 0, 0, 24)
                setOnClickListener {
                    lifecycleScope.launch {
                        val organizerCurrentLandId = ballDeliverySenderCurrentLandId()
                        if (organizerCurrentLandId == null) {
                            showProcessWarning(
                                title = "Cannot choose delivery",
                                message = "The sender current location is unknown."
                            )
                            return@launch
                        }

                        val deliveryCapacityWithoutBards =
                            controller.onValidateBallDeliveryCapacity(
                                currentDraft().copy(
                                    bardPlans = emptyList(),
                                    localMusicianCount = 0
                                )
                            )

                        val updated = if (delegatedToChancellor) {
                            askDelegatedBardSuggestions(
                                candidates = options.bards,
                                existing = chosenBards.toList()
                            )
                        } else {
                            askBardPlans(
                                landId = landId,
                                organizerCurrentLandId = organizerCurrentLandId,
                                candidates = options.bards,
                                existing = chosenBards.toList(),
                                usedMessengersOutsideBards = deliveryCapacityWithoutBards.requiredMessengers,
                                usedDovesOutsideBards = deliveryCapacityWithoutBards.requiredDoves
                            )
                        } ?: return@launch

                        chosenBards.clear()
                        chosenBards.addAll(updated)
                        refreshNameCaches()
                        text = bardSummaryText()
                        inviteeRow.text = inviteeSummaryText()
                        updateWineRow()
                        updateBudgetLabels()
                        updateDeliveryCapacityRow()
                    }
                }
            }
            content.addView(bardSummary)

            venueRow.apply {
                textSize = 18f
                setPadding(0, 0, 0, 24)
                updateVenueRow()
                setOnClickListener {
                    lifecycleScope.launch {
                        if (options.venues.isEmpty()) {
                            showProcessWarning(
                                title = "No venue available",
                                message = "The person who ordered the ball has no manor or palace in this land."
                            )
                            return@launch
                        }

                        val picked = askVenue(options.venues, chosenVenue?.structureId)
                        if (picked != null) {
                            chosenVenue = picked
                            updateVenueRow()
                            updateDeliveryCapacityRow()
                        }
                    }
                }
            }
            content.addView(venueRow)

            inviteeRow.apply {
                text = inviteeSummaryText()
                textSize = 18f
                setPadding(0, 0, 0, 24)
                setOnClickListener {
                    lifecycleScope.launch {
                        refreshPlanningOptions()
                        refreshNameCaches()
                        pruneSelectedActorsThatCannotBeTargeted()

                        text = inviteeSummaryText()
                        updateDeliveryCapacityRow()

                        val organizerCurrentLandId = ballDeliverySenderCurrentLandId()
                        if (organizerCurrentLandId == null) {
                            showProcessWarning(
                                title = "Cannot choose delivery",
                                message = "The sender current location is unknown."
                            )
                            return@launch
                        }

                        val deliveryCapacityWithoutInvitees =
                            controller.onValidateBallDeliveryCapacity(
                                currentDraft().copy(attendeePlans = emptyList())
                            )

                        val updated = if (delegatedToChancellor) {
                            askDelegatedGuestSuggestions(
                                landId = landId,
                                currentMinimumTitleRank = minimumTitleRank,
                                organizerActorId = orderedByActorId,
                                existing = chosenGuests.toList()
                            )
                        } else {
                            askGuestPlans(
                                landId = landId,
                                organizerCurrentLandId = organizerCurrentLandId,
                                currentMinimumTitleRank = minimumTitleRank,
                                organizerActorId = organizerActorId,
                                existing = chosenGuests.toList(),
                                usedMessengersOutsideInvitees = deliveryCapacityWithoutInvitees.requiredMessengers,
                                usedDovesOutsideInvitees = deliveryCapacityWithoutInvitees.requiredDoves
                            )
                        } ?: return@launch

                        minimumTitleRank = updated.first

                        val knownInviteesForSummary = (
                                controller.onGetLocalInviteeSuggestions(
                                    landId = landId,
                                    minimumTitleRank = minimumTitleRank,
                                    organizerActorId = organizerActorId
                                ) + controller.onGetRemoteInviteeSuggestions(
                                    targetLandId = landId,
                                    organizerActorId = organizerActorId
                                )
                                ).distinctBy { it.actorId }

                        knownInviteesForSummary.forEach { candidate ->
                            cachedAttendeeNames[candidate.actorId] = candidate.name
                        }

                        chosenGuests.clear()
                        chosenGuests.addAll(updated.second)

                        if (!delegatedToChancellor) {
                            applyAutoInviteLocalNobles()
                        }

                        refreshPlanningOptions()
                        refreshNameCaches()
                        pruneSelectedActorsThatCannotBeTargeted()

                        updateMinimumTitleRow()
                        text = inviteeSummaryText()
                        bardSummary.text = bardSummaryText()
                        updateWineRow()
                        updateBudgetLabels()
                        updateDeliveryCapacityRow()
                    }
                }
                setOnLongClickListener {
                    lifecycleScope.launch {
                        refreshPlanningOptions()
                        refreshNameCaches()
                        pruneSelectedActorsThatCannotBeTargeted()

                        val pickedRank = askTitleRankChoice(
                            currentRank = minimumTitleRank,
                            titleOptions = titleOptions
                        ) ?: return@launch

                        minimumTitleRank = pickedRank
                        if (!delegatedToChancellor) {
                            applyAutoInviteLocalNobles()
                        }

                        refreshSelectedGuestNamesForSummary(minimumTitleRank)
                        refreshNameCaches()
                        pruneSelectedActorsThatCannotBeTargeted()

                        updateMinimumTitleRow()
                        text = inviteeSummaryText()
                        bardSummary.text = bardSummaryText()
                        updateWineRow()
                        updateBudgetLabels()
                        updateDeliveryCapacityRow()
                    }
                    true
                }
            }
            content.addView(inviteeRow)

            wineRow.apply {
                updateWineRow()
                textSize = 18f
                setPadding(0, 24, 0, 24)
                setOnClickListener {
                    lifecycleScope.launch {
                        if (delegatedToChancellor) {
                            val chosen = askDelegatedAlcoholUsage(delegatedUseAlcohol) ?: return@launch
                            delegatedUseAlcohol = chosen
                            updateWineRow()
                            updateBudgetLabels()
                            updateDeliveryCapacityRow()
                        } else {
                            val recommendation = recommendationForScale(selectedScale)
                            val recommendedUpperBound = when (recommendation.wineText) {
                                "1-14" -> 14
                                "15-42" -> 42
                                else -> 70
                            }

                            val organizerCurrentLandId = ballDeliverySenderCurrentLandId()
                            if (organizerCurrentLandId == null) {
                                showProcessWarning(
                                    title = "Cannot choose wine source",
                                    message = "The sender current location is unknown."
                                )
                                return@launch
                            }

                            val capacityWithoutWine = deliveryCapacityWithoutWineSources()

                            val updated = askStockOnlyWinePlan(
                                landId = landId,
                                organizerCurrentLandId = organizerCurrentLandId,
                                options = options,
                                current = alcoholPlan,
                                recommendedUnits = recommendedUpperBound,
                                usedMessengersOutsideWineSources = capacityWithoutWine.requiredMessengers,
                                usedDovesOutsideWineSources = capacityWithoutWine.requiredDoves
                            )

                            if (updated != null) {
                                alcoholPlan = updated.copy(fromMarket = emptyList())
                                updateWineRow()
                                updateBudgetLabels()
                                updateDeliveryCapacityRow()
                            }
                        }
                    }
                }
            }
            content.addView(wineRow)

            budgetFundingModeRow.apply {
                textSize = 18f
                setPadding(0, 24, 0, 24)
                updateBudgetFundingModeRow()
                setOnClickListener {
                    lifecycleScope.launch {
                        val coinHolderKnown = knownCoinHolderActorIdForBudgetMode() != null
                        val chosen = askBallBudgetFundingMode(
                            currentUsesCoinHolder = budgetUsesCoinHolder,
                            coinHolderKnown = coinHolderKnown,
                            delegatedMode = delegatedToChancellor
                        ) ?: return@launch

                        budgetUsesCoinHolder = chosen

                        if (budgetUsesCoinHolder) {
                            budgetPlan.clear()
                        }

                        updateBudgetFundingModeRow()
                        updateBudgetRow()
                        updateCoinHolderDeliveryRow()
                        updateDeliveryCapacityRow()
                    }
                }
            }
            content.addView(budgetFundingModeRow)

            budgetRow.apply {
                textSize = 18f
                setPadding(0, 24, 0, 24)
                updateBudgetRow()
                setOnClickListener {
                    lifecycleScope.launch {
                        if (budgetUsesCoinHolder) {
                            showProcessWarning(
                                title = "Budget source",
                                message = "This ball currently uses the coin holder for the budget. Switch the funding mode to 'Source personally' to select specific lands."
                            )
                            return@launch
                        }

                        val budgetKnowledgeActorId = budgetFundingKnowledgeActorId()

                        val sourceSelectorCurrentLandId =
                            controller.onGetActualActorLand(budgetKnowledgeActorId)

                        if (sourceSelectorCurrentLandId == null) {
                            showProcessWarning(
                                title = "Cannot choose budget sources",
                                message = "The requestor current location is unknown."
                            )
                            return@launch
                        }

                        val capacityWithoutBudgetSources = deliveryCapacityWithoutBudgetSources()

                        val updated = askBudgetFromStocksPlan(
                            sourceSelectorCurrentLandId = sourceSelectorCurrentLandId,
                            options = options,
                            current = budgetPlan.toList(),
                            usedMessengersOutsideBudgetSources = capacityWithoutBudgetSources.requiredMessengers,
                            usedDovesOutsideBudgetSources = capacityWithoutBudgetSources.requiredDoves
                        ) ?: return@launch

                        budgetPlan.clear()
                        budgetPlan.addAll(updated)
                        updateBudgetRow()
                        updateDeliveryCapacityRow()
                    }
                }
            }
            content.addView(budgetRow)

            coinHolderDeliveryRow.apply {
                textSize = 18f
                setPadding(0, 24, 0, 24)
                updateCoinHolderDeliveryRow()
                setOnClickListener {
                    lifecycleScope.launch {
                        if (!budgetUsesCoinHolder) return@launch

                        val coinHolderId = knownCoinHolderActorIdForBudgetMode()
                        if (coinHolderId == null) {
                            showProcessWarning(
                                title = "Coin holder unavailable",
                                message = if (delegatedToChancellor) {
                                    "No coin holder is known to the organizer."
                                } else {
                                    "No coin holder is known to the requestor."
                                }
                            )
                            return@launch
                        }

                        val viewerActorId = budgetFundingKnowledgeActorId()
                        val viewerCurrentLandId = controller.onGetActualActorLand(viewerActorId)
                        if (viewerCurrentLandId == null) {
                            showProcessWarning(
                                title = "Cannot choose delivery",
                                message = "The sender current location is unknown."
                            )
                            return@launch
                        }

                        val coinHolderLandId =
                            controller.onGetKnownActorLand(viewerActorId, coinHolderId)
                        val allowPersonalVisit =
                            coinHolderLandId != null && coinHolderLandId == viewerCurrentLandId

                        val capacity = currentDeliveryCapacity()

                        val choice = askDeliveryMethodChoiceWithSendLater(
                            current = selectedCoinHolderOrderDeliveryMethod,
                            allowPersonalVisit = allowPersonalVisit,
                            senderLandId = viewerCurrentLandId,
                            selectedMessengers = capacity.requiredMessengers,
                            selectedDoves = capacity.requiredDoves
                        ) ?: return@launch

                        when (choice) {
                            BallDeliveryChoice.PERSONAL_VISIT -> {
                                selectedCoinHolderOrderSendLater = false
                                selectedCoinHolderOrderDeliveryMethod =
                                    if (allowPersonalVisit) DeliveryMethod.LOCAL else DeliveryMethod.MESSENGER
                            }

                            BallDeliveryChoice.DOVE -> {
                                selectedCoinHolderOrderSendLater = false
                                selectedCoinHolderOrderDeliveryMethod = DeliveryMethod.DOVE
                            }

                            BallDeliveryChoice.MESSENGER -> {
                                selectedCoinHolderOrderSendLater = false
                                selectedCoinHolderOrderDeliveryMethod = DeliveryMethod.MESSENGER
                            }

                            BallDeliveryChoice.SEND_LATER -> {
                                selectedCoinHolderOrderSendLater = true
                            }
                        }

                        updateCoinHolderDeliveryRow()
                        updateDeliveryCapacityRow()
                    }
                }
            }
            content.addView(coinHolderDeliveryRow)

            content.addView(baseBudgetLabel)
            content.addView(bardSubtotalLabel)
            content.addView(wineSubtotalLabel)
            content.addView(totalBudgetLabel)

            refreshUiAfterOrganizerChange(showProblems = false)

            val composerDialog = uiProcess.showContentWindow(
                title = "Organize ball",
                contentView = root,
                positiveText = "Save",
                negativeText = "Cancel",
                neutralText = if (existingBallId != null) "Cancel ball" else "Close",
                backgroundResId = R.drawable.dialog_background,
                onPositive = { dialog ->
                    lifecycleScope.launch {
                        val organizerProblems = validateBallOrganizerSelection(
                            orderedByActorId = orderedByActorId,
                            organizerActorId = organizerActorId,
                            targetLandId = landId
                        )

                        if (organizerProblems.isNotEmpty()) {
                            showProcessWarning(
                                title = "Cannot save ball plan",
                                message = organizerProblems.joinToString("\n")
                            )
                            return@launch
                        }

                        if (budgetUsesCoinHolder) {
                            val knownCoinHolder = knownCoinHolderActorIdForBudgetMode()
                            if (knownCoinHolder == null) {
                                showProcessWarning(
                                    title = "Cannot save ball plan",
                                    message = if (delegatedToChancellor) {
                                        "No coin holder is known to the organizer."
                                    } else {
                                        "No coin holder is known to the requestor."
                                    }
                                )
                                return@launch
                            }
                        }

                        val draft = currentDraft()
                        val deliveryValidation = controller.onValidateBallDeliveryCapacity(draft)

                        if (!deliveryValidation.ok) {
                            showProcessWarning(
                                title = if (delegatedToChancellor) {
                                    "Cannot send order to chancellor"
                                } else {
                                    "Cannot save ball plan"
                                },
                                message = deliveryValidation.message
                                    ?: "Invitation delivery capacity is insufficient."
                            )
                            return@launch
                        }

                        val savedState = controller.onSaveBallPlanDraft(
                            draft = draft,
                            existingBallId = existingBallId
                        )

                        dialog.dismiss()

                        showBallPreparationActionsDialog(
                            state = savedState,
                            landId = landId,
                            ballId = savedState.ballId
                        )
                    }
                },
                onNegative = { dialog ->
                    dialog.dismiss()
                },
                onNeutral = { dialog ->
                    lifecycleScope.launch {
                        if (existingBallId != null) {
                            controller.onCancelBallPlan(existingBallId)
                        }
                        dialog.dismiss()
                    }
                }
            )
        }
    }

    private suspend fun askDelegatedBudgetSourcingMode(
        currentUsesCoinHolder: Boolean
    ): Boolean? {
        val options = listOf(
            OptionUi(1L, currentMarker(currentUsesCoinHolder, "Use coin holder")),
            OptionUi(2L, currentMarker(!currentUsesCoinHolder, "Source personally")),
            OptionUi(-1L, "Cancel")
        )

        return pickSecondViewMenu(options) { id ->
            when (id) {
                1L -> true
                2L -> false
                else -> null
            }
        }
    }

    private suspend fun askStockOnlyWinePlan(
        landId: Long,
        organizerCurrentLandId: Long,
        options: BallPlanningOptions,
        current: BallAlcoholPlan,
        recommendedUnits: Int,
        usedMessengersOutsideWineSources: Int = 0,
        usedDovesOutsideWineSources: Int = 0
    ): BallAlcoholPlan? = suspendCancellableCoroutine { cont ->
        lifecycleScope.launch {
            val stockOptions = options.alcoholStocks
                .filter { it.code == "WINE" }
                .sortedWith(compareBy<AlcoholStockOption> { it.landId }.thenBy { it.availableQty })

            val landNameById = linkedMapOf<Long, String>()
            stockOptions.map { it.landId }.distinct().forEach { stockLandId ->
                landNameById[stockLandId] =
                    controller.onGetLandName(stockLandId) ?: "Land $stockLandId"
            }

            val currentByLand = current.fromStocks
                .filter { it.code == "WINE" }
                .associateBy { it.landId }
                .toMutableMap()

            fun wineUsedMessengers(): Int =
                currentByLand.values.count {
                    it.quantity > 0 &&
                            !it.sendLater &&
                            !it.orderSent &&
                            !it.orderDelivered &&
                            it.deliveryMethod == DeliveryMethod.MESSENGER
                }

            fun wineUsedDoves(): Int =
                currentByLand.values.count {
                    it.quantity > 0 &&
                            !it.sendLater &&
                            !it.orderSent &&
                            !it.orderDelivered &&
                            it.deliveryMethod == DeliveryMethod.DOVE
                }

            fun totalUsedMessengers(): Int =
                usedMessengersOutsideWineSources + wineUsedMessengers()

            fun totalUsedDoves(): Int =
                usedDovesOutsideWineSources + wineUsedDoves()

            fun currentTotal(): Int = currentByLand.values.sumOf { it.quantity }

            fun deliverySummary(use: StockUse): String {
                return when {
                    use.sendLater -> "Send later"
                    use.deliveryMethod == DeliveryMethod.LOCAL -> "Deliver personally"
                    use.deliveryMethod == DeliveryMethod.DOVE -> "Via dove"
                    else -> "Via messenger"
                }
            }

            val scrollView = android.widget.ScrollView(this@SecondView).apply {
                isFillViewport = true
            }

            val container = android.widget.LinearLayout(this@SecondView).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(36, 24, 36, 24)
            }
            scrollView.addView(container)

            container.addView(
                android.widget.TextView(this@SecondView).apply {
                    textSize = 18f
                    text = "Recommended wine units: $recommendedUnits"
                    setPadding(0, 0, 0, 12)
                }
            )

            val totalLabel = android.widget.TextView(this@SecondView).apply {
                textSize = 18f
                text = "Selected wine: ${currentTotal()}"
                setPadding(0, 0, 0, 12)
            }
            container.addView(totalLabel)

            val deliveryCapacityLabel = android.widget.TextView(this@SecondView).apply {
                textSize = 18f
                setPadding(0, 0, 0, 24)
            }

            suspend fun updateDeliveryCapacityLabel() {
                val counts = controller.onGetAvailableDeliveryCarriersForLand(organizerCurrentLandId)
                deliveryCapacityLabel.text =
                    "Messengers: ${totalUsedMessengers()} used / ${counts.first} available\n" +
                            "Doves: ${totalUsedDoves()} used / ${counts.second} available"
            }

            updateDeliveryCapacityLabel()
            container.addView(deliveryCapacityLabel)

            if (stockOptions.isEmpty()) {
                container.addView(
                    android.widget.TextView(this@SecondView).apply {
                        textSize = 18f
                        text = "No known wine stocks are available."
                    }
                )

                uiProcess.showContentWindow(
                    title = "Wine from stocks",
                    contentView = scrollView,
                    positiveText = "OK",
                    negativeText = "Cancel",
                    backgroundResId = R.drawable.dialog_background,
                    onPositive = { dialog ->
                        cont.resume(BallAlcoholPlan(fromStocks = emptyList(), fromMarket = emptyList())) {}
                        dialog.dismiss()
                    },
                    onNegative = { dialog ->
                        cont.resume(null) {}
                        dialog.dismiss()
                    }
                )
                return@launch
            }

            stockOptions.forEach { option ->
                val landName = landNameById[option.landId] ?: "Land ${option.landId}"
                val currentUse = currentByLand[option.landId] ?: StockUse(
                    landId = option.landId,
                    code = "WINE",
                    quantity = 0,
                    deliveryMethod = DeliveryMethod.MESSENGER,
                    sendLater = false
                )

                val row = android.widget.LinearLayout(this@SecondView).apply {
                    orientation = android.widget.LinearLayout.VERTICAL
                    setPadding(0, 0, 0, 24)
                }

                row.addView(
                    android.widget.TextView(this@SecondView).apply {
                        textSize = 18f
                        text = "$landName: available ${option.availableQty} wine"
                    }
                )

                val deliveryButton = android.widget.Button(this@SecondView).apply {
                    text = deliverySummary(currentUse)
                }

                row.addView(
                    buildQuantitySliderRow(
                        title = "Quantity",
                        minValue = 0,
                        maxValue = option.availableQty,
                        initialValue = currentUse.quantity
                    ) { updated ->
                        lifecycleScope.launch {
                            val existing = currentByLand[option.landId] ?: currentUse

                            if (updated == 0) {
                                currentByLand.remove(option.landId)
                            } else {
                                currentByLand[option.landId] = existing.copy(quantity = updated)
                            }

                            totalLabel.text = "Selected wine: ${currentTotal()}"
                            deliveryButton.text =
                                currentByLand[option.landId]?.let { deliverySummary(it) }
                                    ?: deliverySummary(currentUse.copy(quantity = 0))

                            updateDeliveryCapacityLabel()
                        }
                    }
                )

                deliveryButton.setOnClickListener {
                    lifecycleScope.launch {
                        val allowPersonalVisit = option.landId == organizerCurrentLandId
                        val existing = currentByLand[option.landId] ?: currentUse

                        val choice = askDeliveryMethodChoiceWithSendLater(
                            current = existing.deliveryMethod,
                            allowPersonalVisit = allowPersonalVisit,
                            senderLandId = organizerCurrentLandId,
                            selectedMessengers = totalUsedMessengers(),
                            selectedDoves = totalUsedDoves()
                        ) ?: return@launch

                        val updated = when (choice) {
                            BallDeliveryChoice.PERSONAL_VISIT ->
                                existing.copy(
                                    deliveryMethod = if (allowPersonalVisit) {
                                        DeliveryMethod.LOCAL
                                    } else {
                                        DeliveryMethod.MESSENGER
                                    },
                                    sendLater = false
                                )

                            BallDeliveryChoice.DOVE ->
                                existing.copy(
                                    deliveryMethod = DeliveryMethod.DOVE,
                                    sendLater = false
                                )

                            BallDeliveryChoice.MESSENGER ->
                                existing.copy(
                                    deliveryMethod = DeliveryMethod.MESSENGER,
                                    sendLater = false
                                )

                            BallDeliveryChoice.SEND_LATER ->
                                existing.copy(sendLater = true)
                        }

                        currentByLand[option.landId] = updated
                        deliveryButton.text = deliverySummary(updated)
                        updateDeliveryCapacityLabel()
                    }
                }

                row.addView(deliveryButton)
                container.addView(row)
            }

            uiProcess.showContentWindow(
                title = "Wine from stocks",
                contentView = scrollView,
                positiveText = "OK",
                negativeText = "Cancel",
                neutralText = "Clear",
                backgroundResId = R.drawable.dialog_background,
                onPositive = { dialog ->
                    val selected = currentByLand.values
                        .filter { it.quantity > 0 }
                        .sortedBy { it.landId }

                    cont.resume(
                        BallAlcoholPlan(
                            fromStocks = selected,
                            fromMarket = emptyList()
                        )
                    ) {}
                    dialog.dismiss()
                },
                onNegative = { dialog ->
                    cont.resume(null) {}
                    dialog.dismiss()
                },
                onNeutral = { dialog ->
                    cont.resume(BallAlcoholPlan(fromStocks = emptyList(), fromMarket = emptyList())) {}
                    dialog.dismiss()
                }
            )
        }
    }

    private suspend fun askBudgetFromStocksPlan(
        sourceSelectorCurrentLandId: Long,
        options: BallPlanningOptions,
        current: List<StockUse>,
        usedMessengersOutsideBudgetSources: Int = 0,
        usedDovesOutsideBudgetSources: Int = 0
    ): List<StockUse>? = suspendCancellableCoroutine { cont ->
        lifecycleScope.launch {
            val budgetOptions = options.budgetStocks
                .filter { it.code == "SILVER" }
                .sortedWith(compareBy<AlcoholStockOption> { it.landId }.thenBy { it.availableQty })

            val landNameById = linkedMapOf<Long, String>()
            budgetOptions.map { it.landId }.distinct().forEach { budgetLandId ->
                landNameById[budgetLandId] =
                    controller.onGetLandName(budgetLandId) ?: "Land $budgetLandId"
            }

            val currentByLand = current
                .filter { it.code == "SILVER" }
                .associateBy { it.landId }
                .toMutableMap()

            fun budgetUsedMessengers(): Int =
                currentByLand.values.count {
                    it.quantity > 0 &&
                            !it.sendLater &&
                            !it.orderSent &&
                            !it.orderDelivered &&
                            it.deliveryMethod == DeliveryMethod.MESSENGER
                }

            fun budgetUsedDoves(): Int =
                currentByLand.values.count {
                    it.quantity > 0 &&
                            !it.sendLater &&
                            !it.orderSent &&
                            !it.orderDelivered &&
                            it.deliveryMethod == DeliveryMethod.DOVE
                }

            fun totalUsedMessengers(): Int =
                usedMessengersOutsideBudgetSources + budgetUsedMessengers()

            fun totalUsedDoves(): Int =
                usedDovesOutsideBudgetSources + budgetUsedDoves()

            fun currentTotal(): Int = currentByLand.values.sumOf { it.quantity }

            fun deliverySummary(use: StockUse): String {
                return when {
                    use.sendLater -> "Send later"
                    use.deliveryMethod == DeliveryMethod.LOCAL -> "Deliver personally"
                    use.deliveryMethod == DeliveryMethod.DOVE -> "Via dove"
                    else -> "Via messenger"
                }
            }

            val scrollView = android.widget.ScrollView(this@SecondView).apply {
                isFillViewport = true
            }

            val container = android.widget.LinearLayout(this@SecondView).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(36, 24, 36, 24)
            }
            scrollView.addView(container)

            if (budgetOptions.isEmpty()) {
                container.addView(
                    android.widget.TextView(this@SecondView).apply {
                        textSize = 18f
                        text = "No known money sources are available."
                    }
                )

                uiProcess.showContentWindow(
                    title = "Budget sources",
                    contentView = scrollView,
                    positiveText = "OK",
                    negativeText = "Cancel",
                    backgroundResId = R.drawable.dialog_background,
                    onPositive = { dialog ->
                        cont.resume(emptyList()) {}
                        dialog.dismiss()
                    },
                    onNegative = { dialog ->
                        cont.resume(null) {}
                        dialog.dismiss()
                    }
                )
                return@launch
            }

            container.addView(
                android.widget.TextView(this@SecondView).apply {
                    textSize = 18f
                    text = "Current land for personal delivery: ${
                        controller.onGetLandName(sourceSelectorCurrentLandId)
                            ?: "Land $sourceSelectorCurrentLandId"
                    }"
                    setPadding(0, 0, 0, 12)
                }
            )

            val totalLabel = android.widget.TextView(this@SecondView).apply {
                textSize = 18f
                text = "Selected budget: ${currentTotal()} silver"
                setPadding(0, 0, 0, 12)
            }
            container.addView(totalLabel)

            val deliveryCapacityLabel = android.widget.TextView(this@SecondView).apply {
                textSize = 18f
                setPadding(0, 0, 0, 24)
            }

            suspend fun updateDeliveryCapacityLabel() {
                val counts = controller.onGetAvailableDeliveryCarriersForLand(sourceSelectorCurrentLandId)
                deliveryCapacityLabel.text =
                    "Messengers: ${totalUsedMessengers()} used / ${counts.first} available\n" +
                            "Doves: ${totalUsedDoves()} used / ${counts.second} available"
            }

            updateDeliveryCapacityLabel()
            container.addView(deliveryCapacityLabel)

            budgetOptions.forEach { option ->
                val landName = landNameById[option.landId] ?: "Land ${option.landId}"
                val currentUse = currentByLand[option.landId] ?: StockUse(
                    landId = option.landId,
                    code = "SILVER",
                    quantity = 0,
                    deliveryMethod = DeliveryMethod.MESSENGER,
                    sendLater = false
                )

                val row = android.widget.LinearLayout(this@SecondView).apply {
                    orientation = android.widget.LinearLayout.VERTICAL
                    setPadding(0, 0, 0, 24)
                }

                row.addView(
                    android.widget.TextView(this@SecondView).apply {
                        textSize = 18f
                        text = "$landName: available ${option.availableQty} silver"
                    }
                )

                val deliveryButton = android.widget.Button(this@SecondView).apply {
                    text = deliverySummary(currentUse)
                }

                row.addView(
                    buildQuantitySliderRow(
                        title = "Quantity",
                        minValue = 0,
                        maxValue = option.availableQty,
                        initialValue = currentUse.quantity
                    ) { updated ->
                        lifecycleScope.launch {
                            val existing = currentByLand[option.landId] ?: currentUse

                            if (updated == 0) {
                                currentByLand.remove(option.landId)
                            } else {
                                currentByLand[option.landId] = existing.copy(quantity = updated)
                            }

                            totalLabel.text = "Selected budget: ${currentTotal()} silver"
                            deliveryButton.text =
                                currentByLand[option.landId]?.let { deliverySummary(it) }
                                    ?: deliverySummary(currentUse.copy(quantity = 0))

                            updateDeliveryCapacityLabel()
                        }
                    }
                )

                deliveryButton.setOnClickListener {
                    lifecycleScope.launch {
                        val allowPersonalVisit = option.landId == sourceSelectorCurrentLandId
                        val existing = currentByLand[option.landId] ?: currentUse

                        val choice = askDeliveryMethodChoiceWithSendLater(
                            current = existing.deliveryMethod,
                            allowPersonalVisit = allowPersonalVisit,
                            senderLandId = sourceSelectorCurrentLandId,
                            selectedMessengers = totalUsedMessengers(),
                            selectedDoves = totalUsedDoves()
                        ) ?: return@launch

                        val updated = when (choice) {
                            BallDeliveryChoice.PERSONAL_VISIT ->
                                existing.copy(
                                    deliveryMethod = if (allowPersonalVisit) {
                                        DeliveryMethod.LOCAL
                                    } else {
                                        DeliveryMethod.MESSENGER
                                    },
                                    sendLater = false
                                )

                            BallDeliveryChoice.DOVE ->
                                existing.copy(
                                    deliveryMethod = DeliveryMethod.DOVE,
                                    sendLater = false
                                )

                            BallDeliveryChoice.MESSENGER ->
                                existing.copy(
                                    deliveryMethod = DeliveryMethod.MESSENGER,
                                    sendLater = false
                                )

                            BallDeliveryChoice.SEND_LATER ->
                                existing.copy(sendLater = true)
                        }

                        currentByLand[option.landId] = updated
                        deliveryButton.text = deliverySummary(updated)
                        updateDeliveryCapacityLabel()
                    }
                }

                row.addView(deliveryButton)
                container.addView(row)
            }

            uiProcess.showContentWindow(
                title = "Budget sources",
                contentView = scrollView,
                positiveText = "OK",
                negativeText = "Cancel",
                neutralText = "Clear",
                backgroundResId = R.drawable.dialog_background,
                onPositive = { dialog ->
                    val selected = currentByLand.values
                        .filter { it.quantity > 0 }
                        .sortedBy { it.landId }

                    cont.resume(selected) {}
                    dialog.dismiss()
                },
                onNegative = { dialog ->
                    cont.resume(null) {}
                    dialog.dismiss()
                },
                onNeutral = { dialog ->
                    cont.resume(emptyList()) {}
                    dialog.dismiss()
                }
            )
        }
    }

    private suspend fun askDelegatedBallScale(
        currentScale: String?
    ): String? {
        val normalized = currentScale?.trim()?.uppercase()

        val options = listOf(
            OptionUi(1L, currentMarker(normalized == "LITTLE", "Little")),
            OptionUi(2L, currentMarker(normalized == "MIDDLE", "Middle")),
            OptionUi(3L, currentMarker(normalized == "BIG", "Big")),
            OptionUi(-1L, "Cancel")
        )

        return pickSecondViewMenu(options) { id ->
            when (id) {
                1L -> "LITTLE"
                2L -> "MIDDLE"
                3L -> "BIG"
                else -> null
            }
        }
    }

    private suspend fun askDelegatedAlcoholUsage(
        currentUseAlcohol: Boolean
    ): Boolean? {
        val options = listOf(
            OptionUi(1L, currentMarker(!currentUseAlcohol, "Do not use alcohol")),
            OptionUi(2L, currentMarker(currentUseAlcohol, "Use alcohol")),
            OptionUi(-1L, "Cancel")
        )

        return pickSecondViewMenu(options) { id ->
            when (id) {
                1L -> false
                2L -> true
                else -> null
            }
        }
    }

    private suspend fun askDelegatedBardSuggestions(
        candidates: List<BardCandidate>,
        existing: List<BardPlanItem>
    ): List<BardPlanItem>? = suspendCancellableCoroutine { cont ->
        val knownIds = candidates.map { it.actorId }.toSet()
        val existingIds = existing
            .filterNot { it.generatedLocally }
            .filter { it.actorId in knownIds }
            .map { it.actorId }
            .toSet()

        val checkedById = candidates.associate { it.actorId to (it.actorId in existingIds) }.toMutableMap()

        val scrollView = android.widget.ScrollView(this).apply {
            isFillViewport = true
        }

        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(36, 24, 36, 24)
        }
        scrollView.addView(container)

        if (candidates.isEmpty()) {
            container.addView(
                android.widget.TextView(this).apply {
                    textSize = 18f
                    text = "No known bards are available."
                }
            )
        } else {
            candidates.forEach { candidate ->
                val checkBox = uiProcess.checkBox(
                    text = "${candidate.name} (notability ${candidate.notabilityLevel})",
                    checked = checkedById[candidate.actorId] == true
                ) { checked ->
                    checkedById[candidate.actorId] = checked
                }
                container.addView(checkBox)
            }
        }

        uiProcess.showContentWindow(
            title = "Suggested bards for chancellor",
            contentView = scrollView,
            positiveText = "OK",
            negativeText = "Cancel",
            backgroundResId = R.drawable.dialog_background,
            onPositive = { dialog ->
                val result = candidates.mapNotNull { candidate ->
                    if (checkedById[candidate.actorId] != true) {
                        null
                    } else {
                        BardPlanItem(
                            actorId = candidate.actorId,
                            deliveryMethod = DeliveryMethod.MESSENGER,
                            sealedLetter = true,
                            oral = false,
                            feeSilver = 0,
                            notabilityLevel = candidate.notabilityLevel,
                            generatedLocally = false,
                            sendLater = true,
                            invitationSent = false
                        )
                    }
                }

                cont.resume(result) {}
                dialog.dismiss()
            },
            onNegative = { dialog ->
                cont.resume(null) {}
                dialog.dismiss()
            }
        )
    }

    private suspend fun askDelegatedGuestSuggestions(
        landId: Long,
        currentMinimumTitleRank: Int,
        organizerActorId: Long,
        existing: List<GuestPlanItem>
    ): Pair<Int, List<GuestPlanItem>>? = suspendCancellableCoroutine { cont ->
        lifecycleScope.launch {
            val local = controller.onGetLocalInviteeSuggestions(
                landId = landId,
                minimumTitleRank = currentMinimumTitleRank,
                organizerActorId = organizerActorId
            )

            val remote = controller.onGetRemoteInviteeSuggestions(
                targetLandId = landId,
                organizerActorId = organizerActorId
            ).filter { it.titleRank >= currentMinimumTitleRank }

            val all = (local + remote).distinctBy { it.actorId }
            val knownIds = all.map { it.actorId }.toSet()
            val existingIds = existing
                .filter { it.actorId in knownIds }
                .map { it.actorId }
                .toSet()

            val checkedById = all.associate { it.actorId to (it.actorId in existingIds) }.toMutableMap()

            val scrollView = android.widget.ScrollView(this@SecondView).apply {
                isFillViewport = true
            }

            val container = android.widget.LinearLayout(this@SecondView).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(36, 24, 36, 24)
            }
            scrollView.addView(container)

            if (all.isEmpty()) {
                container.addView(
                    android.widget.TextView(this@SecondView).apply {
                        textSize = 18f
                        text = "No known invitees are available."
                    }
                )
            } else {
                all.forEach { candidate ->
                    val checkBox = uiProcess.checkBox(
                        text = candidate.name,
                        checked = checkedById[candidate.actorId] == true
                    ) { checked ->
                        checkedById[candidate.actorId] = checked
                    }
                    container.addView(checkBox)
                }
            }

            uiProcess.showContentWindow(
                title = "Suggested invitees for chancellor",
                contentView = scrollView,
                positiveText = "OK",
                negativeText = "Cancel",
                backgroundResId = R.drawable.dialog_background,
                onPositive = { dialog ->
                    val result = all.mapNotNull { candidate ->
                        if (checkedById[candidate.actorId] != true) {
                            null
                        } else {
                            GuestPlanItem(
                                actorId = candidate.actorId,
                                deliveryMethod = DeliveryMethod.MESSENGER,
                                sealedLetter = true,
                                oral = false,
                                sendLater = true,
                                invitationSent = false
                            )
                        }
                    }

                    cont.resume(currentMinimumTitleRank to result) {}
                    dialog.dismiss()
                },
                onNegative = { dialog ->
                    cont.resume(null) {}
                    dialog.dismiss()
                }
            )
        }
    }

    suspend fun validateBallOrganizerSelection(
        orderedByActorId: Long,
        organizerActorId: Long,
        targetLandId: Long
    ): List<String> {
        val problems = mutableListOf<String>()

        if (orderedByActorId <= 0L) {
            problems += "Order giver is not selected."
        }

        if (organizerActorId <= 0L) {
            problems += "Organizer is not selected."
        }

        if (orderedByActorId > 0L && !controller.onCanOrganizeBallInLand(targetLandId, orderedByActorId)) {
            problems += "The person who ordered the ball has no manor or palace in this land."
        }

        if (organizerActorId > 0L && organizerActorId != orderedByActorId) {
            val chancellorActorId = controller.onGetCurrentChancellorActorId()
            when {
                chancellorActorId == null ->
                    problems += "No chancellor is appointed."

                organizerActorId != chancellorActorId ->
                    problems += "The selected organizer is not the appointed chancellor."
            }
        }

        return problems
    }

    private suspend fun askInvitationTransportAndMessageType(
        initialDelivery: DeliveryMethod = DeliveryMethod.MESSENGER,
        initialMessageType: MessageType = MessageType.SEALED_LETTER
    ): Pair<DeliveryMethod, MessageType>? {
        val options = listOf(
            OptionUi(1L, currentMarker(initialDelivery == DeliveryMethod.MESSENGER, "Via messenger / sealed letter")),
            OptionUi(2L, currentMarker(initialDelivery == DeliveryMethod.DOVE, "Via dove / sealed letter")),
            OptionUi(3L, currentMarker(initialDelivery == DeliveryMethod.LOCAL, "Deliver personally / sealed letter")),
            OptionUi(-1L, "Cancel")
        )

        return pickSecondViewMenu(options) { id ->
            when (id) {
                1L -> DeliveryMethod.MESSENGER to MessageType.SEALED_LETTER
                2L -> DeliveryMethod.DOVE to MessageType.SEALED_LETTER
                3L -> DeliveryMethod.LOCAL to MessageType.SEALED_LETTER
                else -> null
            }
        }
    }

    private fun filterInviteesByMinimumTitle(
        candidates: List<AttendeeCandidate>,
        minimumTitleRank: Int
    ): List<AttendeeCandidate> {
        return candidates.filter { it.titleRank >= minimumTitleRank }
    }

    private suspend fun askSendLaterAdjustmentForBall(
        draft: BallPlanDraft,
        availableMessengers: Int,
        availableDoves: Int,
        attendeeNameById: Map<Long, String>,
        bardNameById: Map<Long, String>
    ): BallPlanDraft? = suspendCancellableCoroutine { cont ->
        val guestCandidates = draft.attendeePlans
            .filter { !it.sendLater }
            .map { guest -> "Invitee: ${attendeeNameById[guest.actorId] ?: "Actor ${guest.actorId}"}" to guest }

        val bardCandidates = draft.bardPlans
            .filter { !it.sendLater }
            .map { bard -> "Bard: ${bardNameById[bard.actorId] ?: "Bard ${bard.actorId}"}" to bard }

        val allItems = mutableListOf<Pair<String, Any>>().apply {
            addAll(guestCandidates.map { it.first to it.second })
            addAll(bardCandidates.map { it.first to it.second })
        }

        val checked = BooleanArray(allItems.size) { false }
        val content = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(36, 24, 36, 24)
        }

        content.addView(android.widget.TextView(this).apply {
            text = "Only $availableMessengers messenger deliveries and $availableDoves dove deliveries can be sent now.\nChoose recipients to mark as Send later."
            textSize = 18f
            setPadding(0, 0, 0, 24)
        })

        allItems.forEachIndexed { index, item ->
            content.addView(uiProcess.checkBox(item.first, checked[index]) { value -> checked[index] = value })
        }

        uiProcess.showContentWindow(
            title = "Send later",
            contentView = content,
            positiveText = "Apply",
            negativeText = "Cancel",
            backgroundResId = R.drawable.dialog_background,
            onPositive = { dialog ->
                val guestIdsToDelay = mutableSetOf<Long>()
                val bardKeysToDelay = mutableSetOf<Pair<Long, Boolean>>()
                for (index in allItems.indices) {
                    if (!checked[index]) continue
                    when (val value = allItems[index].second) {
                        is GuestPlanItem -> guestIdsToDelay += value.actorId
                        is BardPlanItem -> bardKeysToDelay += (value.actorId to value.generatedLocally)
                    }
                }
                cont.resume(
                    draft.copy(
                        attendeePlans = draft.attendeePlans.map { guest -> if (guest.actorId in guestIdsToDelay) guest.copy(sendLater = true) else guest },
                        bardPlans = draft.bardPlans.map { bard -> if ((bard.actorId to bard.generatedLocally) in bardKeysToDelay) bard.copy(sendLater = true) else bard }
                    )
                ) {}
                dialog.dismiss()
            },
            onNegative = { dialog -> cont.resume(null) {}; dialog.dismiss() }
        )
    }

    private suspend fun askBallBudgetFundingMode(
        currentUsesCoinHolder: Boolean,
        coinHolderKnown: Boolean,
        delegatedMode: Boolean
    ): Boolean? {
        val options = buildList {
            if (coinHolderKnown) {
                add(OptionUi(1L, currentMarker(currentUsesCoinHolder, "Ask coin holder for budget")))
            }
            add(OptionUi(2L, currentMarker(!currentUsesCoinHolder, "Source budget personally")))
            add(OptionUi(-1L, "Cancel"))
        }

        return pickSecondViewMenu(options) { id ->
            when (id) {
                1L -> true
                2L -> false
                else -> null
            }
        }
    }

    private fun showProcessWarning(
        title: String,
        message: String,
        positiveText: String = "OK",
        onPositive: (() -> Unit)? = null
    ) {
        val content = android.widget.TextView(this).apply {
            text = message
            textSize = 18f
            setPadding(36, 24, 36, 24)
        }

        uiProcess.showContentWindow(
            title = title,
            contentView = content,
            positiveText = positiveText,
            backgroundResId = R.drawable.dialog_background,
            onPositive = { dialog ->
                dialog.dismiss()
                onPositive?.invoke()
            }
        )
    }

    private fun showProcessWarningChoice(
        title: String,
        message: String,
        positiveText: String,
        negativeText: String,
        onPositive: () -> Unit,
        onNegative: (() -> Unit)? = null
    ) {
        val content = android.widget.TextView(this).apply {
            text = message
            textSize = 18f
            setPadding(36, 24, 36, 24)
        }

        uiProcess.showContentWindow(
            title = title,
            contentView = content,
            positiveText = positiveText,
            negativeText = negativeText,
            backgroundResId = R.drawable.dialog_background,
            onPositive = { dialog ->
                dialog.dismiss()
                onPositive()
            },
            onNegative = { dialog ->
                dialog.dismiss()
                onNegative?.invoke()
            }
        )
    }

    private suspend fun askBardPlans(
        landId: Long,
        organizerCurrentLandId: Long,
        candidates: List<BardCandidate>,
        existing: List<BardPlanItem>,
        usedMessengersOutsideBards: Int = 0,
        usedDovesOutsideBards: Int = 0
    ): List<BardPlanItem>? = suspendCancellableCoroutine { cont ->
        lifecycleScope.launch {
            val existingLocalPlans = existing.filter { it.generatedLocally }.toMutableList()
            val existingNamedPlans = existing
                .filterNot { it.generatedLocally }
                .associateBy { it.actorId }
                .toMutableMap()

            val landNameById = mutableMapOf<Long, String>()
            val landIdsToResolve =
                (candidates.mapNotNull { it.lastKnownLandId } + organizerCurrentLandId + landId).distinct()

            for (candidateLandId in landIdsToResolve) {
                val resolved = controller.onGetLandName(candidateLandId)
                if (resolved != null) landNameById[candidateLandId] = resolved
            }

            val included = mutableMapOf<Long, Boolean>().apply {
                put(-999L, existingLocalPlans.isNotEmpty())
                candidates.forEach { candidate ->
                    put(candidate.actorId, existingNamedPlans.containsKey(candidate.actorId))
                }
            }

            fun selectedMessengerCountInsideBards(): Int {
                val localSelected = if (
                    included[-999L] == true &&
                    existingLocalPlans.any { !it.sendLater && !it.invitationSent } &&
                    existingLocalPlans.firstOrNull()?.deliveryMethod == DeliveryMethod.MESSENGER
                ) 1 else 0

                val bardSelected = candidates.count { candidate ->
                    val plan = existingNamedPlans[candidate.actorId]
                    included[candidate.actorId] == true &&
                            plan != null &&
                            !plan.sendLater &&
                            !plan.invitationSent &&
                            plan.deliveryMethod == DeliveryMethod.MESSENGER
                }

                return localSelected + bardSelected
            }

            fun selectedDoveCountInsideBards(): Int {
                val localSelected = if (
                    included[-999L] == true &&
                    existingLocalPlans.any { !it.sendLater && !it.invitationSent } &&
                    existingLocalPlans.firstOrNull()?.deliveryMethod == DeliveryMethod.DOVE
                ) 1 else 0

                val bardSelected = candidates.count { candidate ->
                    val plan = existingNamedPlans[candidate.actorId]
                    included[candidate.actorId] == true &&
                            plan != null &&
                            !plan.sendLater &&
                            !plan.invitationSent &&
                            plan.deliveryMethod == DeliveryMethod.DOVE
                }

                return localSelected + bardSelected
            }

            fun selectedMessengerCount(): Int {
                return usedMessengersOutsideBards + selectedMessengerCountInsideBards()
            }

            fun selectedDoveCount(): Int {
                return usedDovesOutsideBards + selectedDoveCountInsideBards()
            }

            fun defaultNamedBardPlan(candidate: BardCandidate): BardPlanItem {
                return BardPlanItem(
                    actorId = candidate.actorId,
                    deliveryMethod = DeliveryMethod.MESSENGER,
                    sealedLetter = true,
                    oral = false,
                    feeSilver = controller.onGetDefaultBardFeeForNotability(candidate.notabilityLevel),
                    notabilityLevel = candidate.notabilityLevel,
                    generatedLocally = false,
                    sendLater = false,
                    invitationSent = false
                )
            }

            fun deliveryText(plan: BardPlanItem?): String {
                val effective = plan ?: return "via messenger"
                return when {
                    effective.sendLater -> "send later"
                    effective.deliveryMethod == DeliveryMethod.LOCAL -> "deliver personally"
                    effective.deliveryMethod == DeliveryMethod.MESSENGER -> "via messenger"
                    effective.deliveryMethod == DeliveryMethod.DOVE -> "via dove"
                    else -> "via messenger"
                }
            }

            val scrollView = android.widget.ScrollView(this@SecondView).apply { isFillViewport = true }

            val container = android.widget.LinearLayout(this@SecondView).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(36, 24, 36, 24)
            }
            scrollView.addView(container)

            val capacityLabel = android.widget.TextView(this@SecondView).apply {
                textSize = 18f
                setPadding(0, 0, 0, 24)
            }

            suspend fun refreshCapacityLabel() {
                val counts = controller.onGetAvailableDeliveryCarriersForLand(organizerCurrentLandId)
                capacityLabel.text =
                    "Messengers: ${selectedMessengerCount()} used / ${counts.first} available\n" +
                            "Doves: ${selectedDoveCount()} used / ${counts.second} available"
            }

            refreshCapacityLabel()
            container.addView(capacityLabel)

            val localRow = android.widget.LinearLayout(this@SecondView).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(0, 0, 0, 24)
            }

            val localCheckBox = uiProcess.checkBox(
                text = "Use local musicians",
                checked = included[-999L] == true
            ) { checked ->
                included[-999L] = checked
                lifecycleScope.launch { refreshCapacityLabel() }
            }

            val localMusicianSummary = android.widget.TextView(this@SecondView).apply {
                textSize = 16f
                setPadding(0, 8, 0, 8)
            }

            fun refreshLocalMusicianSummary() {
                localMusicianSummary.text = if (existingLocalPlans.isEmpty()) {
                    "No local musicians configured."
                } else {
                    val representative = existingLocalPlans.first()
                    val count = existingLocalPlans.size
                    val delivery = localMusicianDeliverySummary(
                        deliveryMethod = representative.deliveryMethod,
                        sendLater = representative.sendLater
                    )
                    val messageType = localMusicianMessageTypeSummary(
                        oral = representative.oral,
                        sealedLetter = representative.sealedLetter
                    )
                    "$count local musician(s), $delivery, $messageType"
                }
            }

            val configureLocalButton = android.widget.Button(this@SecondView).apply {
                text = "Configure local musicians"
                setOnClickListener {
                    lifecycleScope.launch {
                        val currentPlans = if (existingLocalPlans.isEmpty()) {
                            listOf(
                                BardPlanItem(
                                    actorId = -1L,
                                    deliveryMethod = DeliveryMethod.MESSENGER,
                                    sealedLetter = true,
                                    oral = false,
                                    feeSilver = controller.onGetDefaultBardFeeForNotability(1),
                                    notabilityLevel = 1,
                                    generatedLocally = true,
                                    sendLater = false,
                                    invitationSent = false
                                )
                            )
                        } else {
                            existingLocalPlans
                        }

                        val rebuiltLocalPlans = askLocalMusicianPlan(
                            currentPlans = currentPlans,
                            allowPersonalVisit = organizerCurrentLandId == landId,
                            senderLandId = organizerCurrentLandId
                        )

                        if (rebuiltLocalPlans != null) {
                            existingLocalPlans.clear()
                            existingLocalPlans += rebuiltLocalPlans
                            included[-999L] = rebuiltLocalPlans.isNotEmpty()
                            localCheckBox.isChecked = rebuiltLocalPlans.isNotEmpty()
                            refreshLocalMusicianSummary()
                            refreshCapacityLabel()
                        }
                    }
                }
            }

            refreshLocalMusicianSummary()
            localRow.addView(localCheckBox)
            localRow.addView(localMusicianSummary)
            localRow.addView(configureLocalButton)
            container.addView(localRow)

            candidates.forEach { candidate ->
                val candidateLand = candidate.lastKnownLandId
                val allowPersonalVisit = candidateLand == organizerCurrentLandId
                val initialPlan = existingNamedPlans[candidate.actorId] ?: defaultNamedBardPlan(candidate)

                var currentPlan = if (!allowPersonalVisit && initialPlan.deliveryMethod == DeliveryMethod.LOCAL) {
                    initialPlan.copy(deliveryMethod = DeliveryMethod.MESSENGER, sendLater = false)
                } else {
                    initialPlan
                }

                val standardFee = controller.onGetDefaultBardFeeForNotability(candidate.notabilityLevel)
                currentPlan = currentPlan.copy(
                    feeSilver = if (currentPlan.feeSilver > 0) currentPlan.feeSilver else standardFee,
                    notabilityLevel = candidate.notabilityLevel,
                    sealedLetter = true,
                    oral = false
                )
                existingNamedPlans[candidate.actorId] = currentPlan

                val row = android.widget.LinearLayout(this@SecondView).apply {
                    orientation = android.widget.LinearLayout.VERTICAL
                    setPadding(0, 0, 0, 24)
                }

                val locationText = candidateLand?.let {
                    " — ${landNameById[it] ?: "Unknown land"}"
                } ?: " — location unknown"

                val checkBox = uiProcess.checkBox(
                    text = "${candidate.name}$locationText (notability ${candidate.notabilityLevel})",
                    checked = included[candidate.actorId] == true
                ) { checked ->
                    included[candidate.actorId] = checked
                    lifecycleScope.launch { refreshCapacityLabel() }
                }

                val feeLabel = android.widget.TextView(this@SecondView).apply {
                    textSize = 16f
                    text = "Standard fee: $standardFee"
                    setPadding(0, 8, 0, 8)
                }

                val deliveryButton = android.widget.Button(this@SecondView).apply {
                    text = "Delivery: ${deliveryText(currentPlan)}"
                    setOnClickListener {
                        lifecycleScope.launch {
                            val choice = askDeliveryMethodChoiceWithSendLater(
                                current = currentPlan.deliveryMethod,
                                allowPersonalVisit = allowPersonalVisit,
                                senderLandId = organizerCurrentLandId,
                                selectedMessengers = selectedMessengerCount(),
                                selectedDoves = selectedDoveCount()
                            ) ?: return@launch

                            currentPlan = when (choice) {
                                BallDeliveryChoice.SEND_LATER ->
                                    currentPlan.copy(sendLater = true, sealedLetter = true, oral = false)

                                BallDeliveryChoice.PERSONAL_VISIT -> {
                                    if (allowPersonalVisit) {
                                        currentPlan.copy(
                                            sendLater = false,
                                            deliveryMethod = DeliveryMethod.LOCAL,
                                            sealedLetter = true,
                                            oral = false
                                        )
                                    } else {
                                        currentPlan
                                    }
                                }

                                BallDeliveryChoice.MESSENGER ->
                                    currentPlan.copy(
                                        sendLater = false,
                                        deliveryMethod = DeliveryMethod.MESSENGER,
                                        sealedLetter = true,
                                        oral = false
                                    )

                                BallDeliveryChoice.DOVE ->
                                    currentPlan.copy(
                                        sendLater = false,
                                        deliveryMethod = DeliveryMethod.DOVE,
                                        sealedLetter = true,
                                        oral = false
                                    )
                            }

                            included[candidate.actorId] = true
                            checkBox.isChecked = true
                            existingNamedPlans[candidate.actorId] = currentPlan
                            text = "Delivery: ${deliveryText(currentPlan)}"
                            refreshCapacityLabel()
                        }
                    }
                }

                row.addView(checkBox)
                row.addView(feeLabel)
                row.addView(deliveryButton)
                container.addView(row)
            }

            uiProcess.showContentWindow(
                title = "Choose bards",
                contentView = scrollView,
                positiveText = "OK",
                negativeText = "Cancel",
                backgroundResId = R.drawable.dialog_background,
                onPositive = { dialog ->
                    val result = mutableListOf<BardPlanItem>()

                    if (included[-999L] == true) result += existingLocalPlans

                    candidates.forEach { candidate ->
                        if (included[candidate.actorId] == true) {
                            val old = existingNamedPlans[candidate.actorId] ?: defaultNamedBardPlan(candidate)
                            result += old.copy(sealedLetter = true, oral = false, generatedLocally = false)
                        }
                    }

                    cont.resume(result) {}
                    dialog.dismiss()
                },
                onNegative = { dialog ->
                    cont.resume(null) {}
                    dialog.dismiss()
                }
            )
        }
    }

    private suspend fun askGuestPlans(
        landId: Long,
        organizerCurrentLandId: Long,
        currentMinimumTitleRank: Int,
        organizerActorId: Long,
        existing: List<GuestPlanItem>,
        usedMessengersOutsideInvitees: Int = 0,
        usedDovesOutsideInvitees: Int = 0
    ): Pair<Int, List<GuestPlanItem>>? = suspendCancellableCoroutine { cont ->
        lifecycleScope.launch {
            val titleOptions = controller.onGetBallTitleRankOptions()

            val local = controller.onGetLocalInviteeSuggestions(
                landId = landId,
                minimumTitleRank = currentMinimumTitleRank,
                organizerActorId = organizerActorId
            )

            val remote = controller.onGetRemoteInviteeSuggestions(
                targetLandId = landId,
                organizerActorId = organizerActorId
            ).filter { it.titleRank >= currentMinimumTitleRank }

            val existingMissingCandidates = existing
                .filter { existingPlan ->
                    (local + remote).none { it.actorId == existingPlan.actorId }
                }
                .map { existingPlan ->
                    AttendeeCandidate(
                        actorId = existingPlan.actorId,
                        name = "Actor #${existingPlan.actorId}",
                        lastKnownLandId = null,
                        lastSeenTurn = null,
                        imprisoned = false,
                        titleRank = 0
                    )
                }

            val all = (local + remote + existingMissingCandidates).distinctBy { it.actorId }
            val existingById = existing.associateBy { it.actorId }
            val localIds = local.map { it.actorId }.toSet()
            val useAutomaticLocalSelection = existing.isEmpty()

            val landNameById = mutableMapOf<Long, String>()
            val landIdsToResolve =
                (all.mapNotNull { it.lastKnownLandId } + organizerCurrentLandId + landId).distinct()

            for (candidateLandId in landIdsToResolve) {
                val resolved = controller.onGetLandName(candidateLandId)
                if (resolved != null) landNameById[candidateLandId] = resolved
            }

            val selected = all.associate { candidate ->
                val old = existingById[candidate.actorId]
                candidate.actorId to GuestPlanItem(
                    actorId = candidate.actorId,
                    deliveryMethod = old?.deliveryMethod ?: DeliveryMethod.MESSENGER,
                    sealedLetter = true,
                    oral = false,
                    sendLater = old?.sendLater ?: false,
                    invitationSent = old?.invitationSent ?: false
                )
            }.toMutableMap()

            val included = mutableMapOf<Long, Boolean>().apply {
                all.forEach { candidate ->
                    val isIncluded = if (useAutomaticLocalSelection) {
                        candidate.actorId in localIds
                    } else {
                        candidate.actorId in existingById.keys
                    }
                    put(candidate.actorId, isIncluded)
                }
            }

            fun selectedMessengerCountInsideInvitees(): Int {
                return all.count { candidate ->
                    val plan = selected[candidate.actorId]
                    included[candidate.actorId] == true &&
                            plan != null &&
                            !plan.sendLater &&
                            !plan.invitationSent &&
                            plan.deliveryMethod == DeliveryMethod.MESSENGER
                }
            }

            fun selectedDoveCountInsideInvitees(): Int {
                return all.count { candidate ->
                    val plan = selected[candidate.actorId]
                    included[candidate.actorId] == true &&
                            plan != null &&
                            !plan.sendLater &&
                            !plan.invitationSent &&
                            plan.deliveryMethod == DeliveryMethod.DOVE
                }
            }

            fun selectedMessengerCount(): Int {
                return usedMessengersOutsideInvitees + selectedMessengerCountInsideInvitees()
            }

            fun selectedDoveCount(): Int {
                return usedDovesOutsideInvitees + selectedDoveCountInsideInvitees()
            }

            fun deliveryText(plan: GuestPlanItem?): String {
                val effective = plan ?: return "via messenger"
                return when {
                    effective.sendLater -> "send later"
                    effective.deliveryMethod == DeliveryMethod.LOCAL -> "deliver personally"
                    effective.deliveryMethod == DeliveryMethod.MESSENGER -> "via messenger"
                    effective.deliveryMethod == DeliveryMethod.DOVE -> "via dove"
                    else -> "via messenger"
                }
            }

            val scrollView = android.widget.ScrollView(this@SecondView).apply { isFillViewport = true }

            val container = android.widget.LinearLayout(this@SecondView).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(36, 24, 36, 24)
            }
            scrollView.addView(container)

            val capacityLabel = android.widget.TextView(this@SecondView).apply {
                textSize = 18f
                setPadding(0, 0, 0, 24)
            }

            suspend fun refreshCapacityLabel() {
                val counts = controller.onGetAvailableDeliveryCarriersForLand(organizerCurrentLandId)
                capacityLabel.text =
                    "Messengers: ${selectedMessengerCount()} used / ${counts.first} available\n" +
                            "Doves: ${selectedDoveCount()} used / ${counts.second} available"
            }

            refreshCapacityLabel()
            container.addView(capacityLabel)

            if (all.isEmpty()) {
                container.addView(
                    android.widget.TextView(this@SecondView).apply {
                        textSize = 18f
                        text = "No known invitees are available."
                    }
                )
            }

            all.forEach { candidate ->
                val actorId = candidate.actorId
                val allowPersonalVisit = candidate.lastKnownLandId == organizerCurrentLandId

                var currentPlan = selected[actorId] ?: GuestPlanItem(
                    actorId = actorId,
                    deliveryMethod = DeliveryMethod.MESSENGER,
                    sealedLetter = true,
                    oral = false,
                    sendLater = false,
                    invitationSent = false
                )

                if (!allowPersonalVisit && currentPlan.deliveryMethod == DeliveryMethod.LOCAL) {
                    currentPlan = currentPlan.copy(deliveryMethod = DeliveryMethod.MESSENGER, sendLater = false)
                    selected[actorId] = currentPlan
                }

                val row = android.widget.LinearLayout(this@SecondView).apply {
                    orientation = android.widget.LinearLayout.VERTICAL
                    setPadding(0, 0, 0, 24)
                }

                val titleText = if (candidate.titleRank > 0) {
                    val titleName = titleOptions.firstOrNull { it.rank == candidate.titleRank }?.titleName
                        ?: "rank ${candidate.titleRank}"
                    " [$titleName]"
                } else {
                    ""
                }

                val locationText = candidate.lastKnownLandId?.let { knownLandId ->
                    " — known land: ${landNameById[knownLandId] ?: "Unknown land"}"
                } ?: " — location unknown"

                val checkBox = uiProcess.checkBox(
                    text = "${candidate.name}$titleText$locationText",
                    checked = included[actorId] == true
                ) { checked ->
                    included[actorId] = checked
                    lifecycleScope.launch { refreshCapacityLabel() }
                }

                val deliveryButton = android.widget.Button(this@SecondView).apply {
                    text = "Delivery: ${deliveryText(currentPlan)}"
                    setOnClickListener {
                        lifecycleScope.launch {
                            val choice = askDeliveryMethodChoiceWithSendLater(
                                current = currentPlan.deliveryMethod,
                                allowPersonalVisit = allowPersonalVisit,
                                senderLandId = organizerCurrentLandId,
                                selectedMessengers = selectedMessengerCount(),
                                selectedDoves = selectedDoveCount()
                            ) ?: return@launch

                            currentPlan = when (choice) {
                                BallDeliveryChoice.SEND_LATER ->
                                    currentPlan.copy(sendLater = true, sealedLetter = true, oral = false)

                                BallDeliveryChoice.PERSONAL_VISIT -> {
                                    if (allowPersonalVisit) {
                                        currentPlan.copy(
                                            sendLater = false,
                                            deliveryMethod = DeliveryMethod.LOCAL,
                                            sealedLetter = true,
                                            oral = false
                                        )
                                    } else {
                                        currentPlan
                                    }
                                }

                                BallDeliveryChoice.MESSENGER ->
                                    currentPlan.copy(
                                        sendLater = false,
                                        deliveryMethod = DeliveryMethod.MESSENGER,
                                        sealedLetter = true,
                                        oral = false
                                    )

                                BallDeliveryChoice.DOVE ->
                                    currentPlan.copy(
                                        sendLater = false,
                                        deliveryMethod = DeliveryMethod.DOVE,
                                        sealedLetter = true,
                                        oral = false
                                    )
                            }

                            selected[actorId] = currentPlan
                            included[actorId] = true
                            checkBox.isChecked = true
                            text = "Delivery: ${deliveryText(currentPlan)}"
                            refreshCapacityLabel()
                        }
                    }
                }

                row.addView(checkBox)
                row.addView(deliveryButton)
                container.addView(row)
            }

            uiProcess.showContentWindow(
                title = "Choose invitees",
                contentView = scrollView,
                positiveText = "OK",
                negativeText = "Cancel",
                backgroundResId = R.drawable.dialog_background,
                onPositive = { dialog ->
                    val result = all.mapNotNull { candidate ->
                        if (included[candidate.actorId] != true) {
                            null
                        } else {
                            val plan = selected[candidate.actorId] ?: GuestPlanItem(
                                actorId = candidate.actorId,
                                deliveryMethod = DeliveryMethod.MESSENGER,
                                sealedLetter = true,
                                oral = false,
                                sendLater = false,
                                invitationSent = false
                            )

                            GuestPlanItem(
                                actorId = candidate.actorId,
                                deliveryMethod = plan.deliveryMethod,
                                sealedLetter = true,
                                oral = false,
                                sendLater = plan.sendLater,
                                invitationSent = plan.invitationSent
                            )
                        }
                    }

                    cont.resume(currentMinimumTitleRank to result) {}
                    dialog.dismiss()
                },
                onNegative = { dialog ->
                    cont.resume(null) {}
                    dialog.dismiss()
                }
            )
        }
    }

    private fun showBallFinalizeResultDialog(
        result: GameEngine.BallFinalizeResult,
        delegatedToChancellor: Boolean,
        landId: Long,
        ballId: Long
    ) {
        when (result) {
            is GameEngine.BallFinalizeResult.Success -> {
                showProcessWarning(
                    title = if (delegatedToChancellor) {
                        "Order sent"
                    } else {
                        "Ball released"
                    },
                    message = result.message
                )
            }

            is GameEngine.BallFinalizeResult.Blocked -> {
                showProcessWarningChoice(
                    title = if (delegatedToChancellor) {
                        "Cannot send order to chancellor"
                    } else {
                        "Cannot release ball"
                    },
                    message = result.message,
                    positiveText = "Back to edit",
                    negativeText = "Close",
                    onPositive = {
                        openBallComposer(
                            landId = landId,
                            existingBallId = ballId
                        )
                    }
                )
            }
        }
    }

    private suspend fun askBardDeliveryAndFeeConfig(
        originLandId: Long,
        initial: List<BardPlanItem>,
        landByBardId: Map<Long, Long?>,
        nameByBardId: Map<Long, String>
    ): List<BardPlanItem>? = suspendCancellableCoroutine { cont ->
        val plans = initial.toMutableList()

        fun label(plan: BardPlanItem): String {
            val delivery = when (plan.deliveryMethod) {
                DeliveryMethod.LOCAL -> "deliver personally"
                DeliveryMethod.MESSENGER -> "via messenger"
                DeliveryMethod.DOVE -> "via dove"
            }
            val type = if (plan.sealedLetter) "sealed letter" else if (plan.oral) "oral" else "letter"
            val name = nameByBardId[plan.actorId] ?: "Bard ${plan.actorId}"
            return "$name: fee ${plan.feeSilver}, delivery $delivery, type $type"
        }

        suspend fun chooseNext(): Boolean {
            val selectedIndex = pickSecondViewMenu(
                plans.mapIndexed { index, plan -> OptionUi(index.toLong(), label(plan)) } + OptionUi(-2L, "OK") + OptionUi(-1L, "Cancel")
            ) { id -> id } ?: -1L

            when {
                selectedIndex == -2L -> return false
                selectedIndex == -1L -> {
                    cont.resume(null) {}
                    return false
                }
                selectedIndex.toInt() in plans.indices -> {
                    val which = selectedIndex.toInt()
                    val current = plans[which]
                    val targetLandId = landByBardId[current.actorId] ?: originLandId
                    val allowLocal = targetLandId == originLandId
                    val delivery = askDeliveryMethodChoice(
                        current = if (!allowLocal && current.deliveryMethod == DeliveryMethod.LOCAL) DeliveryMethod.MESSENGER else current.deliveryMethod,
                        allowLocal = allowLocal
                    ) ?: return true
                    val sealedLetter = true
                    val oral = false
                    val fee = askStepperInt(
                        title = "Bard fee",
                        message = "Set fee for ${nameByBardId[current.actorId] ?: "bard"}",
                        initialValue = current.feeSilver,
                        minValue = 0,
                        maxValue = 500,
                        step = 1
                    ) ?: return true
                    plans[which] = current.copy(deliveryMethod = delivery, sealedLetter = sealedLetter, oral = oral, feeSilver = fee)
                    return true
                }
                else -> return true
            }
        }

        lifecycleScope.launch {
            while (chooseNext()) Unit
            if (cont.isActive) cont.resume(plans.toList()) {}
        }
    }

    private suspend fun askGuestDeliveryConfig(
        originLandId: Long,
        initial: List<GuestPlanItem>,
        nameById: Map<Long, String>,
        landById: Map<Long, Long?>
    ): List<GuestPlanItem>? = suspendCancellableCoroutine { cont ->
        val plans = initial.toMutableList()

        fun label(plan: GuestPlanItem): String {
            val delivery = when (plan.deliveryMethod) {
                DeliveryMethod.LOCAL -> "deliver personally"
                DeliveryMethod.MESSENGER -> "via messenger"
                DeliveryMethod.DOVE -> "via dove"
            }
            val type = if (plan.sealedLetter) "sealed letter" else if (plan.oral) "oral" else "letter"
            val name = nameById[plan.actorId] ?: "Invitee ${plan.actorId}"
            return "$name: delivery $delivery, type $type"
        }

        suspend fun chooseNext(): Boolean {
            val selectedIndex = pickSecondViewMenu(
                plans.mapIndexed { index, plan -> OptionUi(index.toLong(), label(plan)) } + OptionUi(-2L, "OK") + OptionUi(-1L, "Cancel")
            ) { id -> id } ?: -1L

            when {
                selectedIndex == -2L -> return false
                selectedIndex == -1L -> {
                    cont.resume(null) {}
                    return false
                }
                selectedIndex.toInt() in plans.indices -> {
                    val which = selectedIndex.toInt()
                    val current = plans[which]
                    val targetLandId = landById[current.actorId] ?: originLandId
                    val allowLocal = targetLandId == originLandId
                    val delivery = askDeliveryMethodChoice(
                        current = if (!allowLocal && current.deliveryMethod == DeliveryMethod.LOCAL) DeliveryMethod.MESSENGER else current.deliveryMethod,
                        allowLocal = allowLocal
                    ) ?: return true
                    plans[which] = current.copy(deliveryMethod = delivery, sealedLetter = true, oral = false)
                    return true
                }
                else -> return true
            }
        }

        lifecycleScope.launch {
            while (chooseNext()) Unit
            if (cont.isActive) cont.resume(plans.toList()) {}
        }
    }

    private suspend fun askTitleRankChoice(
        currentRank: Int,
        titleOptions: List<GameEngine.TitleRankOption>
    ): Int? {
        val options = buildList {
            add(OptionUi(0L, currentMarker(currentRank == 0, "Any known local noble")))
            titleOptions.forEach {
                add(OptionUi(it.rank.toLong(), currentMarker(currentRank == it.rank, it.titleName)))
            }
            add(OptionUi(-1L, "Cancel"))
        }

        return pickSecondViewMenu(options) { id ->
            when {
                id == -1L -> null
                id >= 0L -> id.toInt()
                else -> null
            }
        }
    }

    private suspend fun askDeliveryMethodChoice(
        current: DeliveryMethod,
        allowLocal: Boolean
    ): DeliveryMethod? {
        val options = buildList {
            if (allowLocal) {
                add(OptionUi(1L, currentMarker(current == DeliveryMethod.LOCAL, "Deliver personally")))
            }
            add(OptionUi(2L, currentMarker(current == DeliveryMethod.MESSENGER, "Via messenger")))
            add(OptionUi(3L, currentMarker(current == DeliveryMethod.DOVE, "Via dove")))
            add(OptionUi(-1L, "Cancel"))
        }

        return pickSecondViewMenu(options) { id ->
            when (id) {
                1L -> DeliveryMethod.LOCAL
                2L -> DeliveryMethod.MESSENGER
                3L -> DeliveryMethod.DOVE
                else -> null
            }
        }
    }

    private suspend fun askCombinedWinePlan(
        landId: Long,
        options: BallPlanningOptions,
        current: BallAlcoholPlan,
        recommendedUnits: Int
    ): BallAlcoholPlan? = suspendCancellableCoroutine { cont ->
        lifecycleScope.launch {
            val planStocks = current.fromStocks.toMutableList()
            val planMarket = current.fromMarket.toMutableList()

            val container = android.widget.ScrollView(this@SecondView)
            val content = android.widget.LinearLayout(this@SecondView).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(32, 24, 32, 24)
            }
            container.addView(content)

            val summary = android.widget.TextView(this@SecondView).apply {
                setPadding(0, 0, 0, 24)
                textSize = 18f
            }
            content.addView(summary)

            fun refreshSummary() {
                val total = planStocks.sumOf { it.quantity } + planMarket.sumOf { it.quantity }
                summary.text = "Recommended wine units: $recommendedUnits | Selected: $total"
            }

            options.alcoholStocks.filter { it.code == "WINE" }.forEach { stock ->
                var qty = planStocks.firstOrNull { it.landId == stock.landId && it.code == stock.code }?.quantity ?: 0
                content.addView(
                    buildQuantitySliderRow(
                        title = "Stock wine, land ${stock.landId}, available ${stock.availableQty}",
                        minValue = 0,
                        maxValue = stock.availableQty,
                        initialValue = qty
                    ) { value ->
                        qty = value
                        planStocks.removeAll { it.landId == stock.landId && it.code == stock.code }
                        if (qty > 0) planStocks.add(StockUse(stock.landId, "WINE", qty))
                        refreshSummary()
                    }
                )
            }

            options.marketAlcohol.filter { it.code == "WINE" }.forEach { market ->
                var qty = planMarket.firstOrNull { it.marketLandId == market.landId && it.code == market.code }?.quantity ?: 0
                content.addView(
                    buildQuantitySliderRow(
                        title = "Market wine, land ${market.landId}, price ${market.unitPrice}, available ${market.availableQty}",
                        minValue = 0,
                        maxValue = market.availableQty,
                        initialValue = qty
                    ) { value ->
                        qty = value
                        planMarket.removeAll { it.marketLandId == market.landId && it.code == market.code }
                        if (qty > 0) {
                            planMarket.add(
                                MarketBuy(
                                    marketLandId = market.landId,
                                    code = "WINE",
                                    quantity = qty,
                                    unitPrice = market.unitPrice,
                                    transportDelivery = DeliveryMethod.MESSENGER,
                                    transportSealed = true
                                )
                            )
                        }
                        refreshSummary()
                    }
                )
            }

            refreshSummary()

            uiProcess.showContentWindow(
                title = "Choose wine",
                contentView = container,
                positiveText = "OK",
                negativeText = "Cancel",
                backgroundResId = R.drawable.dialog_background,
                onPositive = { dialog ->
                    cont.resume(BallAlcoholPlan(fromStocks = planStocks.toList(), fromMarket = planMarket.toList())) {}
                    dialog.dismiss()
                },
                onNegative = { dialog -> cont.resume(null) {}; dialog.dismiss() }
            )
        }
    }

    private suspend fun askStepperInt(
        title: String,
        message: String,
        initialValue: Int,
        minValue: Int,
        maxValue: Int,
        step: Int = 1
    ): Int? = suspendCancellableCoroutine { cont ->
        var value = initialValue.coerceIn(minValue, maxValue)

        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 32, 48, 8)
        }

        container.addView(
            android.widget.TextView(this).apply {
                text = message
                setPadding(0, 0, 0, 24)
            }
        )

        container.addView(
            buildQuantitySliderRow(
                title = title,
                minValue = minValue,
                maxValue = maxValue,
                initialValue = value
            ) {
                value = it
            }
        )

        uiProcess.showContentWindow(
            title = title,
            contentView = container,
            positiveText = "OK",
            negativeText = "Cancel",
            backgroundResId = R.drawable.dialog_background,
            onPositive = { dialog ->
                cont.resume(value) {}
                dialog.dismiss()
            },
            onNegative = { dialog ->
                cont.resume(null) {}
                dialog.dismiss()
            }
        )
    }


    private data class InvitationDeliveryChoice(
        val messageType: MessageType,
        val delivery: DeliveryMethod
    )

    private suspend fun askYesNo(
        title: String,
        message: String,
        initialValue: Boolean = false
    ): Boolean {
        return uiProcess.pickMenuWindow(
            options = listOf(
                OptionUi(1L, "Yes${if (initialValue) " (current)" else ""}\n\n$message"),
                OptionUi(2L, "No${if (!initialValue) " (current)" else ""}")
            ),
            backgroundResId = R.drawable.dialog_background,
        ) { result ->
            result.id == 1L
        } ?: initialValue
    }

    private suspend fun askPositiveInt(
        title: String,
        message: String,
        currentValue: Int?
    ): Int? = suspendCancellableCoroutine { cont ->
        var value = currentValue?.takeIf { it > 0 } ?: 1
        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(36, 24, 36, 24)
        }
        container.addView(android.widget.TextView(this).apply { text = message; textSize = 18f })
        container.addView(buildQuantitySliderRow(title, 1, 100000, value) { value = it })
        uiProcess.showContentWindow(
            title = title,
            contentView = container,
            positiveText = "OK",
            negativeText = "Cancel",
            backgroundResId = R.drawable.dialog_background,
            onPositive = { dialog -> cont.resume(value) {}; dialog.dismiss() },
            onNegative = { dialog -> cont.resume(null) {}; dialog.dismiss() }
        )
    }

    private suspend fun showInfoDialog(
        title: String,
        message: String
    ) = suspendCancellableCoroutine<Unit> { cont ->
        showProcessWarning(title = title, message = message, onPositive = { cont.resume(Unit) {} })
    }

    private suspend fun askTurn(
        possibleTurns: List<Int>,
        currentValue: Int?
    ): Int? = suspendCancellableCoroutine { cont ->
        if (possibleTurns.isEmpty()) {
            cont.resume(null) {}
            return@suspendCancellableCoroutine
        }

        var selectedIndex = possibleTurns.indexOf(currentValue)
            .let { if (it >= 0) it else 0 }

        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(36, 24, 36, 24)
        }

        val label = android.widget.TextView(this).apply {
            textSize = 20f
            text = "Turn: ${possibleTurns[selectedIndex]}"
            setPadding(0, 0, 0, 24)
        }

        val row = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val minus = android.widget.Button(this).apply { text = "-" }
        val plus = android.widget.Button(this).apply { text = "+" }

        val slider = android.widget.SeekBar(this).apply {
            max = (possibleTurns.size - 1).coerceAtLeast(0)
            progress = selectedIndex
            layoutParams = android.widget.LinearLayout.LayoutParams(
                0,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        fun setIndex(index: Int) {
            selectedIndex = index.coerceIn(0, possibleTurns.lastIndex)
            slider.progress = selectedIndex
            label.text = "Turn: ${possibleTurns[selectedIndex]}"
        }

        minus.setOnClickListener { setIndex(selectedIndex - 1) }
        plus.setOnClickListener { setIndex(selectedIndex + 1) }

        slider.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: android.widget.SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    setIndex(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) = Unit
        })

        row.addView(minus)
        row.addView(slider)
        row.addView(plus)

        container.addView(label)
        container.addView(row)

        uiProcess.showContentWindow(
            title = "Choose event turn",
            contentView = container,
            positiveText = "OK",
            negativeText = "Cancel",
            backgroundResId = R.drawable.dialog_background,
            onPositive = { dialog ->
                cont.resume(possibleTurns[selectedIndex]) {}
                dialog.dismiss()
            },
            onNegative = { dialog ->
                cont.resume(null) {}
                dialog.dismiss()
            }
        )
    }

    private suspend fun askVenue(
        venues: List<VenueOption>,
        currentStructureId: Long?
    ): VenueOption? {
        if (venues.isEmpty()) return null

        val options = venues.mapIndexed { index, venue ->
            val label = "${venue.name} (${venue.type})"
            OptionUi(
                id = index.toLong(),
                title = currentMarker(venue.structureId == currentStructureId, label)
            )
        } + OptionUi(-1L, "Cancel")

        return pickSecondViewMenu(options) { id ->
            when {
                id == -1L -> null
                id.toInt() in venues.indices -> venues[id.toInt()]
                else -> null
            }
        }
    }

    private suspend fun askActorsMulti(
        title: String,
        actors: List<ActorView>,
        initiallySelectedActorIds: List<Long> = emptyList(),
        extraSingleChoiceLabel: String? = null
    ): List<ActorView> = suspendCancellableCoroutine { cont ->
        val mergedActors = buildList {
            if (extraSingleChoiceLabel != null) add(ActorView(actorId = -999L, name = extraSingleChoiceLabel, imprisoned = false))
            addAll(actors)
        }

        if (mergedActors.isEmpty()) {
            cont.resume(emptyList()) {}
            return@suspendCancellableCoroutine
        }

        val checked = BooleanArray(mergedActors.size) { index -> mergedActors[index].actorId in initiallySelectedActorIds }
        val scrollView = android.widget.ScrollView(this)
        val content = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(36, 24, 36, 24)
        }
        scrollView.addView(content)
        mergedActors.forEachIndexed { index, actor ->
            content.addView(uiProcess.checkBox(actor.name, checked[index]) { checked[index] = it })
        }

        uiProcess.showContentWindow(
            title = title,
            contentView = scrollView,
            positiveText = "OK",
            negativeText = "None",
            backgroundResId = R.drawable.dialog_background,
            onPositive = { dialog ->
                cont.resume(mergedActors.filterIndexed { index, _ -> checked[index] }) {}
                dialog.dismiss()
            },
            onNegative = { dialog -> cont.resume(emptyList()) {}; dialog.dismiss() }
        )
    }

    private suspend fun askDeliveryWithMessageTypeForInvitations(): InvitationDeliveryChoice? {
        val options = listOf(
            OptionUi(1L, "Sealed letter via messenger"),
            OptionUi(2L, "Sealed letter via dove"),
            OptionUi(-1L, "Cancel")
        )

        return pickSecondViewMenu(options) { id ->
            when (id) {
                1L -> InvitationDeliveryChoice(
                    messageType = MessageType.SEALED_LETTER,
                    delivery = DeliveryMethod.MESSENGER
                )
                2L -> InvitationDeliveryChoice(
                    messageType = MessageType.SEALED_LETTER,
                    delivery = DeliveryMethod.DOVE
                )
                else -> null
            }
        }
    }

    private suspend fun askAlcoholPlan(
        options: BallPlanningOptions,
        currentValue: BallAlcoholPlan,
        recommendedTotalUnits: Int
    ): BallAlcoholPlan? {
        val chosenFromStocks = mutableListOf<StockUse>()
        val chosenFromMarket = mutableListOf<MarketBuy>()

        var remainingRecommended = recommendedTotalUnits

        options.alcoholStocks.forEach { stock ->
            val existingQty = currentValue.fromStocks
                .firstOrNull { it.landId == stock.landId && it.code == stock.code }
                ?.quantity

            val initialQty = existingQty ?: minOf(stock.availableQty, remainingRecommended.coerceAtLeast(0))

            val qty = askStepperInt(
                title = "Alcohol from stock",
                message = "Choose quantity of ${stock.code} from land ${stock.landId}. Available: ${stock.availableQty}. Recommended total still uncovered: ${remainingRecommended.coerceAtLeast(0)}",
                initialValue = initialQty,
                minValue = 0,
                maxValue = stock.availableQty,
                step = 1
            ) ?: return null

            if (qty > 0) {
                chosenFromStocks += StockUse(
                    landId = stock.landId,
                    code = stock.code,
                    quantity = qty
                )
                remainingRecommended -= qty
            }
        }

        options.marketAlcohol.forEach { market ->
            val existingQty = currentValue.fromMarket
                .firstOrNull { it.marketLandId == market.landId && it.code == market.code }
                ?.quantity

            val initialQty = existingQty ?: minOf(market.availableQty, remainingRecommended.coerceAtLeast(0))

            val qty = askStepperInt(
                title = "Alcohol from market",
                message = "Choose quantity of ${market.code} from market land ${market.landId}. Available: ${market.availableQty}. Unit price: ${market.unitPrice}. Recommended total still uncovered: ${remainingRecommended.coerceAtLeast(0)}",
                initialValue = initialQty,
                minValue = 0,
                maxValue = market.availableQty,
                step = 1
            ) ?: return null

            if (qty > 0) {
                chosenFromMarket += MarketBuy(
                    marketLandId = market.landId,
                    code = market.code,
                    quantity = qty,
                    unitPrice = market.unitPrice
                )
                remainingRecommended -= qty
            }
        }

        return BallAlcoholPlan(
            fromStocks = chosenFromStocks,
            fromMarket = chosenFromMarket
        )
    }

    private suspend fun askOptionalNote(title: String): String? =
        suspendCancellableCoroutine { cont ->
            val input = android.widget.EditText(this)
            uiProcess.showContentWindow(
                title = title,
                contentView = input,
                positiveText = "OK",
                negativeText = "Skip",
                backgroundResId = R.drawable.dialog_background,
                onPositive = { dialog ->
                    val value = input.text.toString().trim().ifBlank { null }
                    cont.resume(value) {}
                    dialog.dismiss()
                },
                onNegative = { dialog -> cont.resume(null) {}; dialog.dismiss() }
            )
        }

    private suspend fun askActorIdInput(title: String): Long? =
        suspendCancellableCoroutine { cont ->
            val input = android.widget.EditText(this).apply { inputType = android.text.InputType.TYPE_CLASS_NUMBER }
            uiProcess.showContentWindow(
                title = title,
                contentView = input,
                positiveText = "OK",
                negativeText = "Cancel",
                backgroundResId = R.drawable.dialog_background,
                onPositive = { dialog -> cont.resume(input.text.toString().toLongOrNull()) {}; dialog.dismiss() },
                onNegative = { dialog -> cont.resume(null) {}; dialog.dismiss() }
            )
        }

    private suspend fun askCouncilRole(): CouncilRoleProposal? {
        val options = CouncilRoleProposal.values()
        return uiProcess.pickMenuWindow(
            options = options.mapIndexed { index, role ->
                OptionUi(index.toLong(), role.name.replace('_', ' '))
            } + OptionUi(-1L, "Cancel"),
            backgroundResId = R.drawable.dialog_background,
        ) { result ->
            if (result.id == -1L) null else options.getOrNull(result.id.toInt())
        }
    }

    private suspend fun askActorOrderKind(): OrderKind? {
        val options = listOf(
            OrderKind.INVITE_TO_BALL,
            OrderKind.INVITE_TO_FESTIVAL,
            OrderKind.INVITE_TO_RELIGIOUS_FESTIVAL,
            OrderKind.INVITE_TO_HUNT,
            OrderKind.CALL_FOR_DUEL,
            OrderKind.PROPOSE_MARRIAGE,
            OrderKind.PROPOSE_COUNCIL_ROLE,
            OrderKind.JOIN_RETINUE,
            OrderKind.COLLECT_INFO_ABOUT_ACTORS,
            OrderKind.ORDER_KILL_ACTORS,
            OrderKind.CHECK_LOYALTY_ACTORS,
            OrderKind.IMPRISON_ACTORS,
            OrderKind.RELEASE_ACTORS,
            OrderKind.COLLECT_TAX_FROM_ACTORS
        )

        return uiProcess.pickMenuWindow(
            options = options.mapIndexed { index, kind ->
                OptionUi(
                    index.toLong(),
                    when (kind) {
                        OrderKind.INVITE_TO_BALL -> "Invite to ball"
                        OrderKind.INVITE_TO_FESTIVAL -> "Invite to festival"
                        OrderKind.INVITE_TO_RELIGIOUS_FESTIVAL -> "Invite to religious festival"
                        OrderKind.INVITE_TO_HUNT -> "Invite to hunt"
                        OrderKind.CALL_FOR_DUEL -> "Call for duel"
                        OrderKind.PROPOSE_MARRIAGE -> "Propose marriage"
                        OrderKind.PROPOSE_COUNCIL_ROLE -> "Propose council role"
                        OrderKind.JOIN_RETINUE -> "Invite to join retinue"
                        OrderKind.COLLECT_INFO_ABOUT_ACTORS -> "Ask / collect info about actor"
                        OrderKind.ORDER_KILL_ACTORS -> "Order to kill actor"
                        OrderKind.CHECK_LOYALTY_ACTORS -> "Check loyalty"
                        OrderKind.IMPRISON_ACTORS -> "Order imprisonment"
                        OrderKind.RELEASE_ACTORS -> "Order release"
                        OrderKind.COLLECT_TAX_FROM_ACTORS -> "Collect taxes from actor"
                        else -> kind.name
                    }
                )
            } + OptionUi(-1L, "Cancel"),
            backgroundResId = R.drawable.dialog_background,
        ) { result ->
            if (result.id == -1L) null else options.getOrNull(result.id.toInt())
        }
    }

    private suspend fun askDefenseStructureType(): DefenseStructureType? {
        val options = DefenseStructureType.values()
        return uiProcess.pickMenuWindow(
            options = options.mapIndexed { index, type ->
                OptionUi(index.toLong(), type.name.replace('_', ' '))
            } + OptionUi(-1L, "Cancel"),
            backgroundResId = R.drawable.dialog_background,
        ) { result ->
            if (result.id == -1L) null else options.getOrNull(result.id.toInt())
        }
    }

    private suspend fun askFreshInfoRequestMode(
        title: String
    ): FreshInfoRequest? {
        return uiProcess.pickMenuWindow(
            options = listOf(
                OptionUi(1L, "Oral via messenger"),
                OptionUi(2L, "Letter via messenger"),
                OptionUi(3L, "Sealed letter via messenger"),
                OptionUi(4L, "Letter via dove"),
                OptionUi(5L, "Sealed letter via dove"),
                OptionUi(-1L, "Cancel")
            ),
            backgroundResId = R.drawable.dialog_background,
        ) { result ->
            when (result.id) {
                1L -> FreshInfoRequest(MessageType.ORAL, DeliveryMethod.MESSENGER)
                2L -> FreshInfoRequest(MessageType.LETTER, DeliveryMethod.MESSENGER)
                3L -> FreshInfoRequest(MessageType.SEALED_LETTER, DeliveryMethod.MESSENGER)
                4L -> FreshInfoRequest(MessageType.LETTER, DeliveryMethod.DOVE)
                5L -> FreshInfoRequest(MessageType.SEALED_LETTER, DeliveryMethod.DOVE)
                else -> null
            }
        }
    }

    private fun showBallPreparationStateDialog(
        state: BallPreparationState
    ) {
        val message = buildString {
            appendLine("Ball planned for turn ${state.eventTurn}")
            appendLine("Land: ${state.landId}")
            appendLine()
            appendLine("Venue: ${if (state.hasVenue) "Ready" else "Missing"}")
            appendLine("Bards: ${if (state.hasBards) "Ready" else "Missing"}")
            appendLine("Alcohol: ${if (state.hasAlcohol) "Ready" else "Missing"}")
            appendLine("Guests: ${if (state.guestsPrepared) "Ready" else "Missing"}")
            if (state.missingItems.isNotEmpty()) {
                appendLine()
                appendLine("Still needed:")
                state.missingItems.forEach { appendLine("- $it") }
            }
        }

        showProcessWarning(
            title = if (state.ready) "Ball is ready" else "Ball preparation",
            message = message,
            positiveText = "Close"
        )
    }

    private suspend fun showActorListDialog(
        actors: List<ActorView>,
        canAskFresh: Boolean
    ): Boolean = suspendCancellableCoroutine { cont ->
        val message = if (actors.isEmpty()) {
            "No known actors in this land."
        } else {
            actors.joinToString("\n\n") { actor ->
                buildString {
                    appendLine(actor.name)
                    appendLine("Actor ID: ${actor.actorId}")
                    append("Imprisoned: ${if (actor.imprisoned) "Yes" else "No"}")
                }
            }
        }
        val positiveText = if (canAskFresh) "Ask council for fresher info" else "Send request for fresher info"
        uiProcess.showContentWindow(
            title = "Known actors",
            contentView = android.widget.TextView(this).apply { text = message; textSize = 18f; setPadding(36,24,36,24) },
            positiveText = positiveText,
            negativeText = "Close",
            backgroundResId = R.drawable.dialog_background,
            onPositive = { dialog -> cont.resume(true) {}; dialog.dismiss() },
            onNegative = { dialog -> cont.resume(false) {}; dialog.dismiss() }
        )
    }

    private data class MapHit(
        val landId: Long,
        val faction: FactionId?
    )

    private suspend fun askArmyFromList(landId: Long): Long? {
        val armies = controller.getKnownArmiesInLand(landId)
        if (armies.isEmpty()) return null

        return pickSecondViewMenu(
            armies.mapIndexed { index, army -> OptionUi(index.toLong(), "Army #${army.armyId}") } + OptionUi(-1L, "Cancel")
        ) { id ->
            if (id == -1L) null else armies.getOrNull(id.toInt())?.armyId
        }
    }

    private suspend fun askDelivery(): DeliveryMethod? {
        return uiProcess.pickMenuWindow(
            options = listOf(
                OptionUi(1L, "Messenger"),
                OptionUi(2L, "Dove"),
                OptionUi(-1L, "Cancel")
            ),
            backgroundResId = R.drawable.dialog_background,
        ) { result ->
            when (result.id) {
                1L -> DeliveryMethod.MESSENGER
                2L -> DeliveryMethod.DOVE
                else -> null
            }
        }
    }

    private suspend fun askDeliveryOrLocal(landId: Long): DeliveryMethod {
        return if (controller.isPlayerInLand(landId)) {
            DeliveryMethod.LOCAL
        } else {
            askDelivery() ?: DeliveryMethod.MESSENGER
        }
    }

    private suspend fun askActorToMessage(actors: List<ActorView>): ActorView? {
        if (actors.isEmpty()) return null
        return pickSecondViewMenu(
            actors.mapIndexed { index, actor -> OptionUi(index.toLong(), actor.name) } + OptionUi(-1L, "Cancel")
        ) { id ->
            if (id == -1L) null else actors.getOrNull(id.toInt())
        }
    }

    private suspend fun askMessageType(): MessageType? {
        return uiProcess.pickMenuWindow(
            options = listOf(
                OptionUi(1L, "Oral"),
                OptionUi(2L, "Letter"),
                OptionUi(3L, "Sealed letter"),
                OptionUi(-1L, "Cancel")
            ),
            backgroundResId = R.drawable.dialog_background,
        ) { result ->
            when (result.id) {
                1L -> MessageType.ORAL
                2L -> MessageType.LETTER
                3L -> MessageType.SEALED_LETTER
                else -> null
            }
        }
    }

    private suspend fun askBodyText(): String? =
        suspendCancellableCoroutine { cont ->
            val input = android.widget.EditText(this)
            uiProcess.showContentWindow(
                title = "Message body",
                contentView = input,
                positiveText = "Send",
                negativeText = "Cancel",
                backgroundResId = R.drawable.dialog_background,
                onPositive = { dialog -> cont.resume(input.text.toString()) {}; dialog.dismiss() },
                onNegative = { dialog -> cont.resume(null) {}; dialog.dismiss() }
            )
        }

    private suspend fun showLandReportDialog(
        report: ReportResult,
        canAskFresh: Boolean
    ): Boolean = suspendCancellableCoroutine { cont ->
        val resourcesText = if (report.resources.isEmpty()) "Unknown" else report.resources.entries.joinToString("\n") { "${it.key}: ${it.value}" }
        val eventsText = if (report.events.isEmpty()) "None known" else report.events.joinToString("\n") { "${it.first} at turn ${it.second}" }
        val message = buildString {
            appendLine("Known information")
            appendLine()
            appendLine("Land ID: ${report.landId}")
            appendLine("Population: ${report.population ?: "Unknown"}")
            appendLine("Satisfaction: ${report.satisfaction ?: "Unknown"}")
            appendLine("Food turns: ${report.foodTurns ?: "Unknown"}")
            appendLine("State: ${report.state ?: "Unknown"}")
            appendLine()
            appendLine("Resources:")
            appendLine(resourcesText)
            appendLine()
            appendLine("Planned events:")
            append(eventsText)
        }
        val positiveText = if (canAskFresh) "Ask council for fresher info" else "Send request for fresher info"
        uiProcess.showContentWindow(
            title = "Land report",
            contentView = android.widget.TextView(this).apply { text = message; textSize = 18f; setPadding(36,24,36,24) },
            positiveText = positiveText,
            negativeText = "Close",
            backgroundResId = R.drawable.dialog_background,
            onPositive = { dialog -> cont.resume(true) {}; dialog.dismiss() },
            onNegative = { dialog -> cont.resume(false) {}; dialog.dismiss() }
        )
    }

    private suspend fun showArmyListDialog(
        armies: List<com.example.mygame.database.messaging_and_information.ArmyInfo>,
        canAskFresh: Boolean
    ): Boolean = suspendCancellableCoroutine { cont ->
        val message = if (armies.isEmpty()) {
            "No known armies in this land."
        } else {
            armies.joinToString("\n\n") { army ->
                buildString {
                    appendLine("Army #${army.armyId}")
                    appendLine("Faction: ${army.faction}")
                    appendLine("Last known land: ${army.lastKnownLandId}")
                    append("ETA: ${army.movingEta?.let { "${it} turns" } ?: "Not moving / unknown"}")
                }
            }
        }
        val positiveText = if (canAskFresh) "Ask council for fresher info" else "Send request for fresher info"
        uiProcess.showContentWindow(
            title = "Army list",
            contentView = android.widget.TextView(this).apply { text = message; textSize = 18f; setPadding(36,24,36,24) },
            positiveText = positiveText,
            negativeText = "Close",
            backgroundResId = R.drawable.dialog_background,
            onPositive = { dialog -> cont.resume(true) {}; dialog.dismiss() },
            onNegative = { dialog -> cont.resume(false) {}; dialog.dismiss() }
        )
    }
}