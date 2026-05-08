package com.example.mygame.engine_and_helpers.messaging_and_information

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MessagingBindingsModule {

    @Binds
    @Singleton
    abstract fun bindPostOffice(impl: DaoPostOffice): PostOffice

    @Binds
    @Singleton
    abstract fun bindReportIngestion(impl: ReportIngestionImpl): ReportIngestion
}