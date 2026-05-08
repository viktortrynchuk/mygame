package com.example.mygame.engine_and_helpers.persistence_and_game_state

import com.example.mygame.engine_and_helpers.persistence_and_game_state.CommitService
import com.example.mygame.engine_and_helpers.persistence_and_game_state.CommitServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PersistenceBindingsModule {
    @Binds @Singleton
    abstract fun bindCommitService(impl: CommitServiceImpl): CommitService
}