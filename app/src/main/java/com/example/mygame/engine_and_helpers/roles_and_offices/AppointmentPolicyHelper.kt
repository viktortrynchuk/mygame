package com.example.mygame.engine_and_helpers.roles_and_offices

import com.example.mygame.database.roles_and_offices.OfficeAssignmentEntity

/** Gatekeeper helper for appointments: checks basic conflicts/policies. */
class AppointmentPolicyHelper {
    enum class Conflict { NONE, ALREADY_ASSIGNED, TITLE_TOO_LOW }
    data class Result(val allowed: Boolean, val conflict: Conflict)

    fun evaluate(currentOfficeAssignments: List<OfficeAssignmentEntity>, candidateTitles: List<String>, requiredTitle: String?): Result {
        if (currentOfficeAssignments.any { it.endTurn == null }) return Result(false, Conflict.ALREADY_ASSIGNED)
        if (requiredTitle != null && candidateTitles.none { it == requiredTitle }) return Result(false, Conflict.TITLE_TOO_LOW)
        return Result(true, Conflict.NONE)
    }
}