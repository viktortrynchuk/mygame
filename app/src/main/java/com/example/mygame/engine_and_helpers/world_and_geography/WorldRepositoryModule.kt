package com.example.mygame.engine_and_helpers.world_and_geography
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WorldRepositoryModule {
    @Binds @Singleton
    abstract fun bindWorkdRepository(impl: WorldRepositoryImpl):WorldRepository

}