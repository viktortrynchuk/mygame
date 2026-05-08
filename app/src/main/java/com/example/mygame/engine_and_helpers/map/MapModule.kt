package com.example.mygame.engine_and_helpers.map

import com.example.mygame.engine_and_helpers.map.DefaultMapLayoutProvider
import com.example.mygame.engine_and_helpers.map.MapLayoutProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MapModule {

    @Binds
    @Singleton
    abstract fun bindMapLayoutProvider(
        impl: DefaultMapLayoutProvider
    ): MapLayoutProvider

    @Binds
    @Singleton
    abstract fun bindArmySpeedProvider(
        impl: DefaultArmySpeedProvider
    ): ArmySpeedProvider

    @Binds
    @Singleton
    abstract fun bindDefaultKnowledgeRepo(
        impl: DefaultKnowledgeRepo
    ): KnowledgeRepo
}