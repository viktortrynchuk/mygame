package com.example.mygame.views

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.mygame.R
import com.example.mygame.engine_and_helpers.ui.ActivityHost
import com.example.mygame.engine_and_helpers.ui.UiActionController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

//view duel
@AndroidEntryPoint
class FifthView : AppCompatActivity() {
    @Inject
    lateinit var controller: UiActionController
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
        setContentView(R.layout.fifthviewlayout)
        val session   = intent.getLongExtra(EXTRA_SESSION, -1L)

        val returnToMainMenuButton: ImageButton = findViewById(R.id.returnToMainMenuButton5)
        // Set a click listener
        returnToMainMenuButton.setOnClickListener {
            //call the start menu view (twelfth view)
            val intent = Intent(this, TwelfthView::class.java)
            startActivity(intent)
        }
    }
}