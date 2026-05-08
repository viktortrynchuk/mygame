package com.example.mygame.engine_and_helpers.foundations_core

//import com.example.mygame.engine_and_helpers.SystemTimeProvider
//import com.example.mygame.engine_and_helpers.TimeProvider
//import dagger.Binds
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//abstract class AuditBindModule {
//    @Binds @Singleton
//    abstract fun bindAuditLogger(impl: AuditLoggerImpl): AuditLogger
//}
//
//@Module
//@InstallIn(SingletonComponent::class)
//object TimeBindModule {
//    @Provides @Singleton
//    fun provideTimeProvider(): TimeProvider = SystemTimeProvider()
//}