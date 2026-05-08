package com.example.mygame.engine_and_helpers.ui
import android.app.Activity
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class AndroidAppShell @Inject constructor(
    private val activity: Activity
) : AppShell {
    override fun exitApp() {
        activity.finishAffinity()
    }
}