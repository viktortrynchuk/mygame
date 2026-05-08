package com.example.mygame.views

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.mygame.R
import com.example.mygame.database.persistence_and_game_state.CurrentSession
import com.example.mygame.engine_and_helpers.Constants
import com.example.mygame.engine_and_helpers.GameEngine
import com.example.mygame.engine_and_helpers.ui.ActivityHost
import com.example.mygame.engine_and_helpers.ui.UiActionController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TwelfthView : AppCompatActivity() {

    @Inject lateinit var controller: UiActionController
    @Inject lateinit var gameEngine: GameEngine

    private val vm: HostViewModel by viewModels() // assuming you converted it to @HiltViewModel

    override fun onStart() {
        super.onStart()
        ActivityHost.set(this)
    }
    override fun onStop() {
        ActivityHost.set(null)
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.twelfthviewlayout)

        val exitButton: Button = findViewById(R.id.exitGameButton)
        exitButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask()
            } else {
                finishAffinity()
            }
        }

        val startButton: Button = findViewById(R.id.startGameButton)
        startButton.setOnClickListener {
            gameEngine.startGameAsync(lifecycleScope) { session -> routeToStartingView(session) }
        }

        val loadButton: Button = findViewById(R.id.loadGameButton)
        loadButton.setOnClickListener {
            gameEngine.loadGameAsync(lifecycleScope) { session -> routeToStartingView(session) }
        }
    }

    private fun routeToStartingView(session: CurrentSession) {
        if (session == Constants.emptySession) return
        when (session.startingView) {
            1  -> startActivity(Intent(this, FirstView::class.java).putExtra(FirstView.EXTRA_SESSION, session))
            2  -> startActivity(Intent(this, SecondView::class.java).putExtra(SecondView.EXTRA_SESSION, session))
            3  -> startActivity(Intent(this, ThirdView::class.java).putExtra(ThirdView.EXTRA_SESSION, session))
            4  -> startActivity(Intent(this, FourthView::class.java).putExtra(FourthView.EXTRA_SESSION, session))
            5  -> startActivity(Intent(this, FifthView::class.java).putExtra(FifthView.EXTRA_SESSION, session))
            6  -> startActivity(Intent(this, SixthView::class.java).putExtra(SixthView.EXTRA_SESSION, session))
            7  -> startActivity(Intent(this, SeventhView::class.java).putExtra(SeventhView.EXTRA_SESSION, session))
            8  -> startActivity(Intent(this, EighthView::class.java).putExtra(EighthView.EXTRA_SESSION, session))
            9  -> startActivity(Intent(this, NinthView::class.java).putExtra(NinthView.EXTRA_SESSION, session))
            10 -> startActivity(Intent(this, TenthView::class.java).putExtra(TenthView.EXTRA_SESSION, session))
            11 -> startActivity(Intent(this, EleventhView::class.java).putExtra(EleventhView.EXTRA_SESSION, session))
            13 -> startActivity(Intent(this, ThirteenthView::class.java).putExtra(ThirteenthView.EXTRA_SESSION, session))
            14 -> startActivity(Intent(this, FourteenthView::class.java).putExtra(FourteenthView.EXTRA_SESSION, session))
            15 -> startActivity(Intent(this, FifteenthView::class.java).putExtra(FifteenthView.EXTRA_SESSION, session))
            16 -> startActivity(Intent(this, SixteenthView::class.java).putExtra(SixteenthView.EXTRA_SESSION, session))
            else -> startActivity(Intent(this, FirstView::class.java).putExtra(FirstView.EXTRA_SESSION, session))
        }
    }
}
