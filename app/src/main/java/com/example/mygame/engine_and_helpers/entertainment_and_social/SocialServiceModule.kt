package com.example.mygame.engine_and_helpers.entertainment_and_social

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SocialServiceModule {
    @Binds @Singleton
    abstract fun bindJusticeService (impl: SocialServiceImpl): SocialService
}