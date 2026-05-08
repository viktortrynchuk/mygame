package com.example.mygame.engine_and_helpers.ui

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
abstract class UiBindingsModule {

    @Binds @ActivityScoped
    abstract fun bindUiNavigator(impl: UiNavigatorImpl): UiNavigator

    @Binds @ActivityScoped
    abstract fun bindAppShell(impl: AndroidAppShell): AppShell
}