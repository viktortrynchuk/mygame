package com.example.mygame.engine_and_helpers.justice_and_court

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class JusticeServiceModule {
    @Binds @Singleton
    abstract fun bindJusticeService (impl: JusticeServiceImpl): JusticeService
}