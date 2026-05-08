package com.example.mygame.engine_and_helpers.foundations_core


import com.example.mygame.engine_and_helpers.SystemTimeProvider
import com.example.mygame.engine_and_helpers.TimeProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FoundationsBindingsModule {
    @Binds @Singleton
    abstract fun bindTurnClockService(impl: TurnClockServiceImpl): TurnClockService

    @Binds @Singleton
    abstract fun bindAuditLogger(impl: AuditLoggerImpl): AuditLogger

    @Binds @Singleton
    abstract fun bindTimeProvider(impl: SystemTimeProvider): TimeProvider
}