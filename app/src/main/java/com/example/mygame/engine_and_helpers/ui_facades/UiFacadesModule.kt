package com.example.mygame.engine_and_helpers.ui_facades

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UiFacadesModule {
    @Binds @Singleton
    abstract fun bindLandFacade(impl: LandFacadeImpl): LandFacade

    @Binds @Singleton
    abstract fun bindArmyFacade(impl: ArmyFacadeImpl): ArmyFacade

    @Binds @Singleton
    abstract fun bindJusticeFacade(impl: JusticeFacadeImpl): JusticeFacade
}