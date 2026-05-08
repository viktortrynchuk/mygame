package com.example.mygame.database.messaging_and_information

data class LandReportActionState(
    val report: ReportResult,
    val canRefreshLocally: Boolean,
    val refreshRole: String? = null
)
