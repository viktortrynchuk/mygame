package com.example.mygame.views

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mygame.R
import com.example.mygame.engine_and_helpers.ui.ActivityHost
import com.example.mygame.engine_and_helpers.ui.UiActionController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

//View for festival
@AndroidEntryPoint
class EighthView : AppCompatActivity() {
    @Inject lateinit var controller: UiActionController
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
        const val FESTIVAL_ID = "festivalId"
        const val IS_RELIGIOUS = "isReligious"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eighthviewlayout)
        val session   = intent.getLongExtra(EXTRA_SESSION, -1L)
        val festivalId = intent.getLongExtra(FESTIVAL_ID, -1L)
        val isRel = intent.getLongExtra(IS_RELIGIOUS, 0)
        var isReligious = false
        if(isRel.toInt()!=0) isReligious=true

        val returnToMainMenuButton: ImageButton = findViewById(R.id.returnToMainMenuButton8)
        // Set a click listener
        returnToMainMenuButton.setOnClickListener {
            //call the start menu view (twelfth view)
            val intent = Intent(this, TwelfthView::class.java)
            startActivity(intent)
        }

        val returnHomeButton: ImageButton = findViewById(R.id.returnToHomeButton8)
        // Set a click listener
        returnToMainMenuButton.setOnClickListener {
        // call the home view (FirstView)
            val intent = Intent(this, FirstView::class.java)
            startActivity(intent)
        }


        val joinFestival: Button = findViewById(R.id.joinFestival)
        // Set a click listener
        joinFestival.setOnClickListener {
            // call the processing of the festival

//            lifecycleScope.launch {
//                controller.onFestivalJoin(festivalId, isReligious)
//            }

        }
    }


}