package com.example.mygame.views

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import com.example.mygame.R
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.mygame.database.messaging_and_information.DeliveryMethod
import com.example.mygame.engine_and_helpers.CouncilContact
import com.example.mygame.engine_and_helpers.RoleMenuChoice
import com.example.mygame.engine_and_helpers.UIProcess
import com.example.mygame.engine_and_helpers.ui.ActivityHost
import com.example.mygame.engine_and_helpers.ui.UiActionController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

// view room
@AndroidEntryPoint
class FirstView : AppCompatActivity() {
    @Inject
    lateinit var controller: UiActionController
    private lateinit var returnToMainMenuButton: ImageButton
    private lateinit var skipSeveralTurnsButton: ImageButton
    private lateinit var endTurnButton: ImageButton
    private lateinit var overlay: ImageView

    override fun onStart() {
        super.onStart()
        ActivityHost.set(this)
    }
    override fun onStop() {
        ActivityHost.set(null)
        super.onStop()
    }
    //example of passing parameters
    companion object {
        const val EXTRA_SESSION = "session"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.firstviewlayout)
        //example of passing parameters
        val session = intent.getLongExtra(EXTRA_SESSION, -1L)

        returnToMainMenuButton = findViewById(R.id.returnToMainMenuButton)
        overlay = findViewById(R.id.overlay)
        endTurnButton = findViewById(R.id.endTurnButton)
        skipSeveralTurnsButton = findViewById(R.id.skipSeveralTurnsButton)

        //to make buttons clickable
        val topBar = findViewById<View>(R.id.topBar)
        ViewCompat.setOnApplyWindowInsetsListener(topBar) { v, insets ->
            val t = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            v.setPadding(v.paddingLeft, v.paddingTop + t, v.paddingRight, v.paddingBottom)
            insets
        }
        topBar.bringToFront()
        ViewCompat.setElevation(topBar, 16f)

        // Set a click listener
        returnToMainMenuButton.setOnClickListener {
            //call the start menu view (twelfth view)
            val intent = Intent(this, TwelfthView::class.java)
            startActivity(intent)

        }

        // Set a click listener
        endTurnButton.setOnClickListener {
            lifecycleScope.launch { controller.onClickEndTurn() }
        }

        // Set a click listener
        skipSeveralTurnsButton.setOnClickListener {
            lifecycleScope.launch { controller.onClickSkipTurns(3)}
        }

        val map = findViewById<MaskClickableImageView>(R.id.regionMap)
        map.setImageResource(R.drawable.home_east_room_day)

        map.mask = BitmapFactory.decodeResource(
            resources, R.drawable.view1_if_the_town_background_mask,
            BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_8888 }
        )
        map.onRegionClick = { regionId ->
            when (regionId) {
                rgbOf(R.color.black)  ->
                    lifecycleScope.launch {
                        controller.onClickMapFromRoom()
                    }
                rgbOf(R.color.blue)   -> {
                    lifecycleScope.launch {
                        val ui = UIProcess(this@FirstView)
//                        val who = ui.pickWhoToCall() ?: return@launch
                        val who = ui.pickCouncilContact() ?: return@launch
                        hideChromeShowOverlayFor(who)

                        overlay.doOnLayout {
                            lifecycleScope.launch {
                                val choice = ui.pickRoleMenu(who)
                                when (who) {
                                    CouncilContact.CHANCELLOR -> handleChancellorChoice(choice)
                                    CouncilContact.DEFENCE_COMMANDER -> handleDefenseChoice(choice)
                                    CouncilContact.COIN_HOLDER -> handleCoinHolderChoice(choice)
                                }
                                restoreChromeHideOverlay()
                            }
                        }
                    }
                }
            }
        }
    }

    // Small helpers to keep code readable
    private fun hideChromeShowOverlayFor(who: CouncilContact) {
        returnToMainMenuButton.isVisible = false
        skipSeveralTurnsButton.isVisible = false
        endTurnButton.isVisible = false

        val img = when (who) {
            CouncilContact.CHANCELLOR -> R.drawable.man2
            CouncilContact.DEFENCE_COMMANDER -> R.drawable.bard
            CouncilContact.COIN_HOLDER -> R.drawable.man1
        }
        overlay.setImageResource(img)
        overlay.alpha = 0.9f
        overlay.isVisible = true
        overlay.requestLayout()
    }

    private fun restoreChromeHideOverlay() {
        overlay.isVisible = false
        returnToMainMenuButton.isVisible = true
        skipSeveralTurnsButton.isVisible = true
        endTurnButton.isVisible = true
    }

    /** Delegate the chosen *actions* to UiActionController (which calls GameEngine) */
    private suspend fun handleChancellorChoice(choice: RoleMenuChoice?) {
        when (choice) {
            null -> Unit
            RoleMenuChoice.LAST_ORDER_STATUS -> controller.onClickNobleReport("CHANCELLOR", via = DeliveryMethod.LOCAL)
            RoleMenuChoice.GENERAL_REPORT    -> controller.onClickNobleReport("CHANCELLOR", via = DeliveryMethod.LOCAL)
            RoleMenuChoice.HIRE_SPY          -> controller.onTavernHire("spy", targetId = null)
            RoleMenuChoice.ORGANIZE_BALL     -> controller.onOrganizeBallHere()
            RoleMenuChoice.ORGANIZE_FESTIVAL -> controller.onOrganizeFestivalHere(isReligious = false)
            RoleMenuChoice.ORGANIZE_RELIGIOUS_FESTIVAL -> controller.onOrganizeFestivalHere(isReligious = true)
            RoleMenuChoice.ORGANIZE_HUNT     -> controller.onOrganizeHuntHere()
            RoleMenuChoice.SEND_MESSAGE      -> {
                // You can show a small dialog to select a target. For now, just open Audience view.
                controller.onClickNobleReport("CHANCELLOR", DeliveryMethod.MESSENGER)
            }
            RoleMenuChoice.SEND_MESSAGE_DOVE -> controller.onClickNobleReport("CHANCELLOR", DeliveryMethod.DOVE)
            else -> Unit
        }
    }

    private suspend fun handleDefenseChoice(choice: RoleMenuChoice?) {
        when (choice) {
            null -> Unit
            RoleMenuChoice.LAST_ORDER_STATUS -> controller.onClickNobleReport("DEFENSE_COMMANDER", via = DeliveryMethod.LOCAL)
            RoleMenuChoice.DEFENCE_STATUS    -> controller.onClickNobleReport("DEFENSE_COMMANDER", via = DeliveryMethod.LOCAL)
            RoleMenuChoice.HIRE_RECRUITER    -> controller.onTavernHire("recruiting_agent", targetId = null)
            else -> Unit
        }
    }

    private suspend fun handleCoinHolderChoice(choice: RoleMenuChoice?) {
        when (choice) {
            null -> Unit
            RoleMenuChoice.LAST_ORDER_STATUS -> controller.onClickNobleReport("COIN_HOLDER", via = DeliveryMethod.LOCAL)
            RoleMenuChoice.ECONOMICS_STATUS  -> controller.onClickNobleReport("COIN_HOLDER", via = DeliveryMethod.LOCAL)
            RoleMenuChoice.NEED_MONEY        -> controller.onTavernHire("merchant", targetId = null) // or a dedicated flow
            RoleMenuChoice.HIRE_TAX_COLLECTOR-> controller.onTavernHire("tax_collector", targetId = null)
            RoleMenuChoice.CHANGE_TAXES      -> { /* open a taxes screen; when confirmed, call a controller method */ }
            RoleMenuChoice.SELL_GOODS        -> { /* open sell dialog; then controller/game call */ }
            RoleMenuChoice.BUY_GOODS         -> { /* open buy dialog; then controller/game call */ }
            else -> Unit
        }
    }

    // Helper to get RGB int from colors.xml (drop alpha)
    fun Context.rgbOf(@androidx.annotation.ColorRes id: Int) =
        androidx.core.content.ContextCompat.getColor(this, id) and 0x00FF_FFFF

}