package com.example.mygame.engine_and_helpers

import android.app.Application
import com.example.mygame.engine_and_helpers.test_seeder.AppInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class MyGameApp : Application() {
    @Inject
    lateinit var initializer: AppInitializer
    override fun onCreate() {
        super.onCreate()
        initializer.init()
    }
}