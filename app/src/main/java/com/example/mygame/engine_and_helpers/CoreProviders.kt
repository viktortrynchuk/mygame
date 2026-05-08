package com.example.mygame.engine_and_helpers

import com.example.mygame.database.AppDatabase
import com.example.mygame.engine_and_helpers.TxRunner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreProviders {
    @Provides @Singleton
    fun provideTxRunner(db: AppDatabase): TxRunner = TxRunner(db)
}