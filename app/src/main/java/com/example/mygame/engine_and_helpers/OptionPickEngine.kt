package com.example.mygame.engine_and_helpers

import android.view.View
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentResultListener
import com.example.mygame.R
import com.example.mygame.views.OptionUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.coroutines.resume
import androidx.fragment.app.DialogFragment

data class OptionPickResult(val id: Long, val position: Int)
class OptionPickEngine(private val activity: FragmentActivity) {

    private var usedWindowFlag = false

    fun launchPickOptions(
        scope: CoroutineScope,
        optionsProvider: suspend () -> List<OptionUi>,
        showGuardOnConfirm: Boolean = true,
        touchGuardId: Int = R.id.touchGuard,
        onResult: (OptionPickResult?) -> Unit
    ) {
        scope.launch {
            val r = pickOptions(optionsProvider, showGuardOnConfirm, touchGuardId)
            onResult(r)
        }
    }

    /** Optional: show the freeze overlay (or fall back to window flag) */
    fun showGuard(touchGuardId: Int = R.id.touchGuard) {
        val guard = activity.findViewById<View?>(touchGuardId)
        if (guard != null) {
            guard.visibility = View.VISIBLE
        } else {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            usedWindowFlag = true
        }
    }

    /** Hide the freeze overlay (or clear the window flag) */
    fun hideGuard(touchGuardId: Int = R.id.touchGuard) {
        val guard = activity.findViewById<View?>(touchGuardId)
        if (guard != null) guard.visibility = View.GONE
        if (usedWindowFlag) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            usedWindowFlag = false
        }
    }

    /**
     * Reusable option picker:
     * - optionsProvider: suspend block that returns list of OptionUi
     * - showGuardOnConfirm: show guard when user presses OK (caller later hides it)
     * - touchGuardId: your overlay view id (if present)
     *
     * Returns OptionPickResult or null if user cancelled.
     */
    @Volatile private var isShowing = false

    companion object {
        private const val TAG_PICKER = "OptionPickerDialog"       // small chooser
        private const val TAG_FULL   = "FullScreenOptionDialog"   // role menu
    }

    suspend fun pickOptions(
        optionsProvider: suspend () -> List<OptionUi>,
        showGuardOnConfirm: Boolean = true,
        touchGuardId: Int = R.id.touchGuard
    ): OptionPickResult? {
        val options = withContext(Dispatchers.IO) { optionsProvider() }
        if (options.isEmpty()) return null

        if (isShowing) return null

        val fm = activity.supportFragmentManager
        fm.executePendingTransactions() // <-- ensure previous removals are applied

        val existing = fm.findFragmentByTag(TAG_PICKER)
        if (existing?.isAdded == true) return null

        isShowing = true
        val requestKey = "dialog_result_" + UUID.randomUUID().toString()

        return suspendCancellableCoroutine { cont ->
            val listener = FragmentResultListener { key, bundle ->
                if (key != requestKey) return@FragmentResultListener
                fm.clearFragmentResultListener(requestKey)
                isShowing = false

                val cancelled = bundle.getBoolean("cancelled", false)
                if (cancelled) {
                    if (!cont.isCompleted) cont.resume(null)
                    return@FragmentResultListener
                }

                val pos = bundle.getInt("position")
                val id  = bundle.getLong("chosenId")
                if (showGuardOnConfirm) showGuard(touchGuardId)
                if (!cont.isCompleted) cont.resume(OptionPickResult(id, pos))
            }

            fm.setFragmentResultListener(requestKey, activity, listener)

            OptionPickerDialog
                .newInstance(ArrayList(options), requestKey)
                .show(fm, TAG_PICKER)

            cont.invokeOnCancellation {
                fm.clearFragmentResultListener(requestKey)
                isShowing = false
            }
        }
    }

    /** NEW/ensure present: full-screen variant with its own tag */
    suspend fun pickOptionsFullScreen(
        optionsProvider: suspend () -> List<OptionUi>,
        excludeRightPx: Int,
        showGuardOnConfirm: Boolean = false,
        backgroundResId: Int = 0
    ): OptionPickResult? {
        val options = withContext(Dispatchers.IO) { optionsProvider() }
        if (options.isEmpty()) return null
        if (isShowing) return null

        val fm = activity.supportFragmentManager
        fm.executePendingTransactions()
        val existing = fm.findFragmentByTag(TAG_FULL)
        if (existing?.isAdded == true) return null

        isShowing = true
        val requestKey = "dialog_result_" + UUID.randomUUID().toString()

        return suspendCancellableCoroutine { cont ->
            val listener = FragmentResultListener { key, bundle ->
                if (key != requestKey) return@FragmentResultListener
                fm.clearFragmentResultListener(requestKey)
                isShowing = false

                val cancelled = bundle.getBoolean("cancelled", false)
                if (cancelled) {
                    if (!cont.isCompleted) cont.resume(null)
                    return@FragmentResultListener
                }

                val pos = bundle.getInt("position")
                val id  = bundle.getLong("chosenId")
                if (showGuardOnConfirm) showGuard()
                if (!cont.isCompleted) cont.resume(OptionPickResult(id, pos))
            }

            fm.setFragmentResultListener(requestKey, activity, listener)

            FullScreenOptionDialog
                .newInstance(ArrayList(options), requestKey, excludeRightPx, backgroundResId)
                .show(fm, TAG_FULL)

            cont.invokeOnCancellation {
                fm.clearFragmentResultListener(requestKey)
                isShowing = false
            }
        }
    }
}