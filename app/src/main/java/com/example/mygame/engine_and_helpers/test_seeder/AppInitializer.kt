package com.example.mygame.engine_and_helpers.test_seeder

import com.example.mygame.engine_and_helpers.AppCoroutineModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppInitializer @Inject constructor(
    private val seeder: DbSeeder,
    @AppCoroutineModule.ApplicationScope private val appScope: CoroutineScope
) {
    fun init() {
        appScope.launch(Dispatchers.IO) {
            seeder.seedAllIfEmpty()
        }
    }
}