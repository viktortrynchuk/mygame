package com.example.mygame.engine_and_helpers.ui_facades

import com.example.mygame.engine_and_helpers.messaging_and_information.MessagingService
import com.example.mygame.engine_and_helpers.messaging_and_information.MessagingServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MessagingFacadeBindingsModule {

    @Binds @Singleton
    abstract fun bindMessagingService(impl: MessagingServiceImpl): MessagingService

    @Binds @Singleton
    abstract fun bindMessagingFacade(impl: MessagingFacadeImpl): MessagingFacade
}