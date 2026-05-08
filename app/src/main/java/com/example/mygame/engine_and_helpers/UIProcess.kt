package com.example.mygame.engine_and_helpers

import android.R.attr.onClick
import android.app.Dialog
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.mygame.R
import com.example.mygame.views.OptionUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

enum class CouncilContact { CHANCELLOR, DEFENCE_COMMANDER, COIN_HOLDER }

/** Reusable UI wrapper around OptionPickEngine and custom full-screen process windows. */
class UIProcess(private val activity: FragmentActivity) {

    private val picker = OptionPickEngine(activity)

    suspend fun <T> pickOne(
        options: List<OptionUi>,
        showGuardOnConfirm: Boolean = true,
        touchGuardId: Int = R.id.touchGuard,
        mapper: (OptionPickResult) -> T?
    ): T? {
        val res = picker.pickOptions(
            optionsProvider = { options },
            showGuardOnConfirm = showGuardOnConfirm,
            touchGuardId = touchGuardId
        ) ?: return null

        if (showGuardOnConfirm) picker.hideGuard(touchGuardId)
        return mapper(res)
    }

    fun <T> pickOneAsync(
        scope: CoroutineScope,
        optionsProvider: () -> List<OptionUi>,
        showGuardOnConfirm: Boolean = true,
        touchGuardId: Int = R.id.touchGuard,
        mapper: (OptionPickResult) -> T?,
        onResult: (T?) -> Unit
    ) {
        picker.launchPickOptions(
            scope = scope,
            optionsProvider = optionsProvider,
            showGuardOnConfirm = showGuardOnConfirm,
            touchGuardId = touchGuardId
        ) { res ->
            if (showGuardOnConfirm) picker.hideGuard(touchGuardId)
            onResult(res?.let(mapper))
        }
    }

    suspend fun <T> pickMenuFullScreen(
        options: List<OptionUi>,
        rightPanelId: Int = R.id.rightPanel,
        backgroundResId: Int,
        showGuardOnConfirm: Boolean = false,
        mapper: (OptionPickResult) -> T?
    ): T? {
        val res = picker.pickOptionsFullScreen(
            optionsProvider = { options },
            excludeRightPx = rightPanelWidth(rightPanelId),
            showGuardOnConfirm = showGuardOnConfirm,
            backgroundResId = backgroundResId
        ) ?: return null

        if (showGuardOnConfirm) picker.hideGuard(R.id.touchGuard)
        return mapper(res)
    }

    fun showContentFullScreen(
        title: String,
        contentView: View,
        positiveText: String,
        negativeText: String? = null,
        neutralText: String? = null,
        rightPanelId: Int = R.id.rightPanel,
        backgroundResId: Int = R.drawable.dialog_background,
        positiveImageResId: Int? = null,
        negativeImageResId: Int? = null,
        neutralImageResId: Int? = null,
        positiveBackgroundResId: Int? = null,
        negativeBackgroundResId: Int? = null,
        neutralBackgroundResId: Int? = null,
        onPositive: (Dialog) -> Unit,
        onNegative: ((Dialog) -> Unit)? = null,
        onNeutral: ((Dialog) -> Unit)? = null
    ): Dialog {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val root = FrameLayout(activity).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        root.addView(
            ImageView(activity).apply {
                setImageResource(backgroundResId)
                scaleType = ImageView.ScaleType.CENTER_CROP
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            }
        )

        val contentArea = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            ).apply {
                rightMargin = rightPanelWidth(rightPanelId)
            }
        }

        contentArea.addView(
            TextView(activity).apply {
                text = title
                textSize = 24f
                setPadding(0, 0, 0, 24)
            }
        )

        (contentView.parent as? ViewGroup)?.removeView(contentView)

        contentArea.addView(
            contentView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val buttonRow = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
            setPadding(0, 24, 0, 0)
        }

        if (neutralText != null) {
            buttonRow.addView(
                actionButton(
                    text = neutralText,
                    imageResId = neutralImageResId,
                    backgroundResId = neutralBackgroundResId
                ) {
                    onNeutral?.invoke(dialog) ?: dialog.dismiss()
                }
            )
        }

        if (negativeText != null) {
            buttonRow.addView(
                actionButton(
                    text = negativeText,
                    imageResId = negativeImageResId,
                    backgroundResId = negativeBackgroundResId
                ) {
                    onNegative?.invoke(dialog) ?: dialog.dismiss()
                }
            )
        }

        buttonRow.addView(
            actionButton(
                text = positiveText,
                imageResId = positiveImageResId,
                backgroundResId = positiveBackgroundResId
            ) {
                onPositive(dialog)
            }
        )

        contentArea.addView(buttonRow)
        root.addView(contentArea)

        dialog.setContentView(root)
        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        return dialog
    }

    fun imageButton(
        imageResId: Int,
        contentDescription: String,
        backgroundResId: Int? = null,
        width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
        height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
        onClick: () -> Unit
    ): ImageButton {
        return ImageButton(activity).apply {
            setImageResource(imageResId)
            this.contentDescription = contentDescription
            if (backgroundResId != null) {
                setBackgroundResource(backgroundResId)
            }
            setOnClickListener { onClick() }
            layoutParams = LinearLayout.LayoutParams(width, height).apply {
                setMargins(8, 8, 8, 8)
            }
        }
    }

    fun checkBox(
        text: String,
        checked: Boolean,
        enabled: Boolean = true,
        onChanged: (Boolean) -> Unit
    ): CheckBox {
        return CheckBox(activity).apply {
            this.text = text
            isChecked = checked
            isEnabled = enabled
            setOnCheckedChangeListener { _, value -> onChanged(value) }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 8)
            }
        }
    }

    fun slider(
        title: String,
        minValue: Int,
        maxValue: Int,
        currentValue: Int,
        step: Int = 1,
        enabled: Boolean = true,
        onChanged: (Int) -> Unit
    ): LinearLayout {
        val safeStep = step.coerceAtLeast(1)
        val safeMin = minValue.coerceAtMost(maxValue)
        val safeMax = maxValue.coerceAtLeast(minValue)
        val safeCurrent = currentValue.coerceIn(safeMin, safeMax)

        val root = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            isEnabled = enabled
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 16)
            }
        }

        val label = TextView(activity).apply {
            text = "$title: $safeCurrent"
        }

        val seekBar = SeekBar(activity).apply {
            max = safeMax - safeMin
            progress = safeCurrent - safeMin
            isEnabled = enabled

            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    val snapped = (progress / safeStep) * safeStep
                    val value = (safeMin + snapped).coerceIn(safeMin, safeMax)

                    if (fromUser && snapped != progress) {
                        seekBar?.progress = snapped
                    }

                    label.text = "$title: $value"
                    onChanged(value)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            })
        }

        root.addView(label)
        root.addView(seekBar)

        return root
    }

    suspend fun pickCouncilContact(): CouncilContact? {
        val options = listOf(
            OptionUi(1L, "call the chancellor"),
            OptionUi(2L, "call the defence commander"),
            OptionUi(3L, "call the coin holder")
        )
        return pickOne(
            options = options,
            showGuardOnConfirm = true,
            touchGuardId = R.id.touchGuard
        ) { res ->
            when (res.id) {
                1L -> CouncilContact.CHANCELLOR
                2L -> CouncilContact.DEFENCE_COMMANDER
                3L -> CouncilContact.COIN_HOLDER
                else -> null
            }
        }
    }

    fun pickCouncilContactAsync(
        scope: CoroutineScope,
        onResult: (CouncilContact?) -> Unit
    ) {
        pickOneAsync(
            scope = scope,
            optionsProvider = {
                listOf(
                    OptionUi(1L, "call the chancellor"),
                    OptionUi(2L, "call the defence commander"),
                    OptionUi(3L, "call the coin holder")
                )
            },
            showGuardOnConfirm = true,
            touchGuardId = R.id.touchGuard,
            mapper = { res ->
                when (res.id) {
                    1L -> CouncilContact.CHANCELLOR
                    2L -> CouncilContact.DEFENCE_COMMANDER
                    3L -> CouncilContact.COIN_HOLDER
                    else -> null
                }
            },
            onResult = onResult
        )
    }

    suspend fun pickRoleMenu(role: CouncilContact): RoleMenuChoice? {
        val (options, mapping, bg) = when (role) {
            CouncilContact.CHANCELLOR -> Triple(
                listOf(
                    OptionUi(1, "How my last order is executed?"),
                    OptionUi(2, "Give me general report"),
                    OptionUi(3, "Hire a spy"),
                    OptionUi(4, "Organize a ball"),
                    OptionUi(5, "Organize a festival"),
                    OptionUi(6, "Organize a hunt"),
                    OptionUi(7, "Send a message to"),
                    OptionUi(-1, "Nevermind")
                ),
                mapOf(
                    1L to RoleMenuChoice.LAST_ORDER_STATUS,
                    2L to RoleMenuChoice.GENERAL_REPORT,
                    3L to RoleMenuChoice.HIRE_SPY,
                    4L to RoleMenuChoice.ORGANIZE_BALL,
                    5L to RoleMenuChoice.ORGANIZE_FESTIVAL,
                    6L to RoleMenuChoice.ORGANIZE_HUNT,
                    7L to RoleMenuChoice.SEND_MESSAGE
                ),
                R.drawable.dialog_background
            )

            CouncilContact.DEFENCE_COMMANDER -> Triple(
                listOf(
                    OptionUi(1, "How my last order is executed?"),
                    OptionUi(2, "What is the general situation with defence?"),
                    OptionUi(3, "Hire a recruiting agent"),
                    OptionUi(-1, "Nevermind")
                ),
                mapOf(
                    1L to RoleMenuChoice.LAST_ORDER_STATUS,
                    2L to RoleMenuChoice.DEFENCE_STATUS,
                    3L to RoleMenuChoice.HIRE_RECRUITER
                ),
                R.drawable.dialog_background
            )

            CouncilContact.COIN_HOLDER -> Triple(
                listOf(
                    OptionUi(1, "How my last order is executed?"),
                    OptionUi(2, "What is the general situation with economics?"),
                    OptionUi(3, "I need money"),
                    OptionUi(4, "Hire the tax collector"),
                    OptionUi(5, "Lets change taxes"),
                    OptionUi(6, "Lets sell some goods"),
                    OptionUi(7, "Lets buy some goods"),
                    OptionUi(-1, "Nevermind")
                ),
                mapOf(
                    1L to RoleMenuChoice.LAST_ORDER_STATUS,
                    2L to RoleMenuChoice.ECONOMICS_STATUS,
                    3L to RoleMenuChoice.NEED_MONEY,
                    4L to RoleMenuChoice.HIRE_TAX_COLLECTOR,
                    5L to RoleMenuChoice.CHANGE_TAXES,
                    6L to RoleMenuChoice.SELL_GOODS,
                    7L to RoleMenuChoice.BUY_GOODS
                ),
                R.drawable.dialog_background
            )
        }

        return pickMenuFullScreen(
            options = options,
            rightPanelId = R.id.rightPanel,
            backgroundResId = bg,
            showGuardOnConfirm = false
        ) { res ->
            if (res.id == -1L) null else mapping[res.id]
        }
    }

    private fun actionButton(
        text: String,
        imageResId: Int?,
        backgroundResId: Int?,
        onClick: () -> Unit
    ): View {
        return if (imageResId != null) {
            imageButton(
                imageResId = imageResId,
                contentDescription = text,
                backgroundResId = backgroundResId,
                onClick = onClick
            )
        } else {
            Button(activity).apply {
                this.text = text
                isAllCaps = false
                setTextColor(android.graphics.Color.WHITE)
                if (backgroundResId != null) {
                    setBackgroundResource(backgroundResId)
                }
                setOnClickListener { onClick() }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 8, 8, 8)
                }
            }
        }
    }

    private fun rightPanelWidth(rightPanelId: Int): Int {
        val rp = activity.findViewById<View?>(rightPanelId)
        return when {
            rp == null -> 0
            rp.width > 0 -> rp.width
            rp.measuredWidth > 0 -> rp.measuredWidth
            rp.layoutParams?.width ?: 0 > 0 -> rp.layoutParams!!.width
            else -> 0
        }
    }

    fun showContentWindow(
        title: String,
        contentView: View,
        positiveText: String,
        negativeText: String? = null,
        neutralText: String? = null,
        extraText: String? = null,
        backgroundResId: Int = R.drawable.dialog_background,
        widthRatio: Float = 0.82f,
        heightRatio: Float = 0.86f,
        onPositive: (Dialog) -> Unit,
        onNegative: ((Dialog) -> Unit)? = null,
        onNeutral: ((Dialog) -> Unit)? = null,
        onExtra: ((Dialog) -> Unit)? = null
    ): Dialog {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val displayMetrics = activity.resources.displayMetrics
        val targetWidth = (displayMetrics.widthPixels * widthRatio).toInt()
        val targetHeight = (displayMetrics.heightPixels * heightRatio).toInt()

        val root = FrameLayout(activity).apply {
            layoutParams = ViewGroup.LayoutParams(targetWidth, targetHeight)
        }

        root.addView(
            ImageView(activity).apply {
                setImageResource(backgroundResId)
                scaleType = ImageView.ScaleType.CENTER_CROP
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            }
        )

        val contentArea = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        if (title.isNotBlank()) {
            contentArea.addView(
                TextView(activity).apply {
                    text = title
                    textSize = 24f
                    setTextColor(android.graphics.Color.WHITE)
                    setPadding(0, 0, 0, 24)
                }
            )
        }

        (contentView.parent as? ViewGroup)?.removeView(contentView)

        contentArea.addView(
            contentView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val buttonRow = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
            setPadding(0, 24, 0, 0)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        if (extraText != null) {
            buttonRow.addView(actionButton(extraText, null, null) {
                onExtra?.invoke(dialog) ?: dialog.dismiss()
            })
        }

        if (negativeText != null) {
            buttonRow.addView(actionButton(negativeText, null, null) {
                onNegative?.invoke(dialog) ?: dialog.dismiss()
            })
        }

        if (positiveText.isNotBlank()) {
            buttonRow.addView(actionButton(positiveText, null, null) {
                onPositive(dialog)
            })
        }

        if (buttonRow.childCount > 0) {
            contentArea.addView(buttonRow)
        }

        root.addView(contentArea)

        dialog.setContentView(root)
        dialog.show()

        dialog.window?.setLayout(targetWidth, targetHeight)

        return dialog
    }

    suspend fun <T> pickMenuWindow(
        options: List<OptionUi>,
        title: String = "",
        backgroundResId: Int = R.drawable.dialog_background,
        widthRatio: Float = 0.82f,
        mapper: (OptionPickResult) -> T?
    ): T? = suspendCancellableCoroutine { cont ->

        val scrollView = android.widget.ScrollView(activity).apply {
            isFillViewport = true
        }

        val container = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 8, 16, 8)
        }

        scrollView.addView(container)

        lateinit var dialog: Dialog

        options.forEach { option ->
            val button = Button(activity).apply {
                text = option.title
                isAllCaps = false

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 8, 0, 8)
                }
            }

            button.setOnClickListener {
                val result = mapper(
                    OptionPickResult(
                        id = option.id,
                        position = options.indexOf(option)
                    )
                )

                if (cont.isActive) {
                    cont.resume(result) {}
                }

                dialog.dismiss()
            }

            container.addView(button)
        }

        dialog = showContentWindow(
            title = title,
            contentView = scrollView,
            positiveText = "",
            negativeText = null,
            neutralText = null,
            backgroundResId = backgroundResId,
            widthRatio = widthRatio,
            onPositive = { }
        )

        dialog.setOnCancelListener {
            if (cont.isActive) {
                cont.resume(null) {}
            }
        }

        cont.invokeOnCancellation {
            dialog.dismiss()
        }
    }
}