package com.example.mygame.engine_and_helpers.messaging_and_information

data class StructuredResponse(
    val requested: String,
    val landDetails: List<String> = emptyList(),
    val countryDetails: List<String> = emptyList(),
    val armyDetails: List<String> = emptyList(),
    val actorDetails: List<String> = emptyList(),
    val loyaltyDetails: List<String> = emptyList(),
    val resources: List<ResourceAmount> = emptyList(),
    val answerYes: Boolean? = null
)

object ResponseComposer {

    fun compose(response: StructuredResponse): String {
        return buildString {
            appendLine("REQUESTED=${response.requested}")

            if (response.landDetails.isNotEmpty()) {
                appendLine("LAND_DETAILS=${response.landDetails.joinToString(";")}")
            }
            if (response.countryDetails.isNotEmpty()) {
                appendLine("COUNTRY_DETAILS=${response.countryDetails.joinToString(";")}")
            }
            if (response.armyDetails.isNotEmpty()) {
                appendLine("ARMY_DETAILS=${response.armyDetails.joinToString(";")}")
            }
            if (response.actorDetails.isNotEmpty()) {
                appendLine("ACTOR_DETAILS=${response.actorDetails.joinToString(";")}")
            }
            if (response.loyaltyDetails.isNotEmpty()) {
                appendLine("LOYALTY=${response.loyaltyDetails.joinToString(";")}")
            }
            if (response.resources.isNotEmpty()) {
                appendLine(
                    "RESOURCES=" + response.resources.joinToString(";") {
                        "${it.code}:${it.quantity}"
                    }
                )
            }
            response.answerYes?.let {
                append("ANSWER=")
                append(if (it) "YES" else "NO")
            }
        }.trim()
    }
}