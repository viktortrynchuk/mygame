package com.example.mygame.engine_and_helpers.messaging_and_information

object OrderComposer {

    fun compose(draft: MessageDraft): String {
        validate(draft)

        return buildString {
            append("ORDER_KIND=")
            append(draft.orderKind.name)

            appendSection("LANDS", draft.lands)
            appendSection("STRUCTURES", draft.structureIds)
            appendSection("ACTORS", draft.actorIds)
            appendSection("COUNTRIES", draft.countryIds)
            appendSection("ARMIES", draft.armyIds)
            appendSection("ENEMY_ARMIES", draft.enemyArmyIds)
            appendSection("TITLES_GRANT", draft.titleIdsToGrant)
            appendSection("TITLES_TERMINATE", draft.titleIdsToTerminate)

            draft.targetLandId?.let {
                append("|TARGET_LAND=")
                append(it)
            }
            draft.targetActorId?.let {
                append("|TARGET_ACTOR=")
                append(it)
            }
            draft.targetCountryId?.let {
                append("|TARGET_COUNTRY=")
                append(it)
            }
            draft.defenseStructureType?.let {
                append("|DEFENSE_STRUCTURE_TYPE=")
                append(it.name)
            }
            draft.scheduledTurn?.let {
                append("|SCHEDULED_TURN=")
                append(it)
            }
            draft.relatedEventId?.let {
                append("|RELATED_EVENT=")
                append(it)
            }
            if (draft.inviteLocalPeople) {
                append("|INVITE_LOCAL_PEOPLE=1")
            }
            if (draft.allowConvoyHire) {
                append("|ALLOW_CONVOY_HIRE=1")
            }

            if (draft.resourceAmounts.isNotEmpty()) {
                append("|RESOURCES=")
                append(
                    draft.resourceAmounts.joinToString(";") {
                        "${it.code}:${it.quantity}"
                    }
                )
            }

            draft.marriageProposal?.let {
                append("|MARRIAGE=")
                append(it.actorAId)
                append(":")
                append(it.actorBId)
            }

            draft.councilRoleProposal?.let {
                append("|COUNCIL_ROLE=")
                append(it.role.name)
                append(":")
                append(it.actorId)
            }

            draft.freeTextNote?.takeIf { it.isNotBlank() }?.let {
                append("|NOTE=")
                append(it.sanitize())
            }
        }
    }

    private fun validate(draft: MessageDraft) {
        val required = OrderRequirements.requiredFields(draft.orderKind)

        fun requireField(ok: Boolean, name: String) {
            require(ok) { "Missing required field: $name for ${draft.orderKind}" }
        }

        if (DraftField.LANDS in required) requireField(draft.lands.isNotEmpty(), "lands")
        if (DraftField.STRUCTURES in required) requireField(draft.structureIds.isNotEmpty(), "structures")
        if (DraftField.ACTORS in required) requireField(draft.actorIds.isNotEmpty(), "actors")
        if (DraftField.RESOURCES in required) requireField(draft.resourceAmounts.isNotEmpty(), "resources")
        if (DraftField.COUNTRIES in required) requireField(draft.countryIds.isNotEmpty(), "countries")
        if (DraftField.ARMIES in required) requireField(draft.armyIds.isNotEmpty(), "armies")
        if (DraftField.ENEMY_ARMIES in required) requireField(draft.enemyArmyIds.isNotEmpty(), "enemy armies")
        if (DraftField.TITLES_TO_GRANT in required) requireField(draft.titleIdsToGrant.isNotEmpty(), "titles to grant")
        if (DraftField.TITLES_TO_TERMINATE in required) requireField(draft.titleIdsToTerminate.isNotEmpty(), "titles to terminate")
        if (DraftField.TARGET_LAND in required) requireField(draft.targetLandId != null, "target land")
        if (DraftField.TARGET_ACTOR in required) requireField(draft.targetActorId != null, "target actor")
        if (DraftField.TARGET_COUNTRY in required) requireField(draft.targetCountryId != null, "target country")
        if (DraftField.MARRIAGE_PROPOSAL in required) requireField(draft.marriageProposal != null, "marriage proposal")
        if (DraftField.COUNCIL_ROLE_PROPOSAL in required) requireField(draft.councilRoleProposal != null, "council role proposal")
        if (DraftField.SCHEDULED_TURN in required) requireField(draft.scheduledTurn != null, "scheduled turn")
    }

    private fun StringBuilder.appendSection(name: String, ids: List<Long>) {
        if (ids.isNotEmpty()) {
            append("|")
            append(name)
            append("=")
            append(ids.joinToString(","))
        }
    }

    private fun String.sanitize(): String =
        replace("\n", " ").replace("\r", " ").replace("|", "/")
}