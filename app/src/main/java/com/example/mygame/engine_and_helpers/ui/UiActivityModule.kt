package com.example.mygame.engine_and_helpers.ui

import android.content.Context
import com.example.mygame.engine_and_helpers.Constants
import com.example.mygame.engine_and_helpers.ui.AppShell
import com.example.mygame.engine_and_helpers.ui.UiNavigator
import com.example.mygame.engine_and_helpers.ui.DefaultRandom
import com.example.mygame.engine_and_helpers.ui.RandomProvider
import com.example.mygame.database.persistence_and_game_state.CurrentSession
import com.example.mygame.dao.SessionDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

/**
 * If you already have a DatabaseModule that provides SessionDAO,
 * keep that as-is. We only wire UI-scoped objects here.
 */
@Module
@InstallIn(dagger.hilt.android.components.ActivityComponent::class)
object UiActivityModule {

    @Provides
    @ActivityScoped
    fun provideRandomProvider(): RandomProvider = DefaultRandom()

    @Provides
    @ActivityScoped
    fun provideCurrentSession(sessionDao: SessionDAO): CurrentSession =
        runBlocking { sessionDao.getAllSessions().firstOrNull() ?: Constants.emptySession }
}
