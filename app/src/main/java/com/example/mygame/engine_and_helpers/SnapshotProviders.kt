package com.example.mygame.engine_and_helpers

import com.example.mygame.engine_and_helpers.persistence_and_game_state.WorldSnapshotProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SnapshotProviders {
    @Provides @Singleton
    fun provideWorldSnapshotProvider(): WorldSnapshotProvider = object : WorldSnapshotProvider {
        override fun snapshot(): ByteArray = byteArrayOf() // TODO: plug in your real snapshot
    }
}