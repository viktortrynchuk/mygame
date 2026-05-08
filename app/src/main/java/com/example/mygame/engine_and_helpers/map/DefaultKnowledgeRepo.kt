package com.example.mygame.engine_and_helpers.map

import androidx.room.PrimaryKey
import javax.inject.Inject
import javax.inject.Singleton
import com.example.mygame.dao.armies_units_warfare.ArmyDao
import com.example.mygame.dao.messaging_and_information.KnowledgeDao
import com.example.mygame.dao.movements_logistics_supplies.MovementOrderDao
import com.example.mygame.dao.movements_logistics_supplies.PathSegmentDao
import com.example.mygame.database.messaging_and_information.ArmyKnowledge
import com.example.mygame.database.messaging_and_information.GarrisonKnowledge
import com.example.mygame.database.messaging_and_information.KnowledgeEntryEntity
import com.example.mygame.database.messaging_and_information.MovementKnowledge
import com.example.mygame.database.messaging_and_information.SiegeKnowledge
import com.example.mygame.database.movements_logistics_supplies.MovementOrderEntity
import com.example.mygame.database.movements_logistics_supplies.PathSegmentEntity

@Singleton
class DefaultKnowledgeRepo @Inject constructor(
    private val armyDao: ArmyDao,
    private val moveDao: MovementOrderDao,
    private val pathDao: PathSegmentDao,
    private val knowledgeDao: KnowledgeDao,
) : KnowledgeRepo {

    override suspend fun knownArmiesForActor(actorId: Long): List<ArmyKnowledge> {
        return lastKnownArmyLandByFaction(actorId)
    }
    override suspend fun lastKnownArmyLandByFaction(actorId: Long): List<ArmyKnowledge> {
        val armies = armyDao.all() // TODO: use knowledge table when you have it
        return armies.map {
            ArmyKnowledge(
                armyId = it.id,
                landId = it.landId,
                factionIndex = ((it.countryId ?: 7L).toInt() % 8),
                turn = 0
            )
        }
    }

    override suspend fun knownSieges(actorId: Long): List<SiegeKnowledge> {
        val entries = knowledgeDao.factsByPrefix(actorId, "SIEGE|")

        // Keep latest entry per siege key (land + attackerArmy)
        val latestByKey = LinkedHashMap<String, KnowledgeEntryEntity>()
        for (e in entries) {
            val parsed = parseSiegeFact(e.fact) ?: continue
            val key = "${parsed.defenderLandId}:${parsed.attackerArmyId}"
            if (latestByKey[key] == null) {
                latestByKey[key] = e // entries are sorted DESC by turn
            }
        }

        return latestByKey.values
            .mapNotNull { e ->
                val parsed = parseSiegeFact(e.fact) ?: return@mapNotNull null

                // If you added confirmed boolean:
                if (!e.confirmed) return@mapNotNull null

                // If you did NOT add confirmed boolean (Option B), interpret from fact:
                // if (!parsed.confirmed) return@mapNotNull null

                SiegeKnowledge(
                    attackerFactionIndex = parsed.attackerFactionIndex,
                    defenderLandId = parsed.defenderLandId,
                    turn = parsed.startedTurn
                )
            }
    }
    private data class ParsedSiege(
        val defenderLandId: Long,
        val attackerArmyId: Long,
        val startedTurn: Int,
        val attackerFactionIndex: Int,
        val confirmed: Boolean = true // only used if you embed truth in fact
    )

    private fun parseSiegeFact(fact: String): ParsedSiege? {
        // expected: SIEGE|land=12|attackerArmy=44|startedTurn=10|attackerFaction=3
        if (!fact.startsWith("SIEGE|")) return null

        val parts = fact.split('|').drop(1)
        val map = parts.mapNotNull {
            val i = it.indexOf('=')
            if (i <= 0) null else it.substring(0, i) to it.substring(i + 1)
        }.toMap()

        val land = map["land"]?.toLongOrNull() ?: return null
        val attackerArmy = map["attackerArmy"]?.toLongOrNull() ?: return null
        val startedTurn = map["startedTurn"]?.toIntOrNull() ?: return null
        val attackerFaction = map["attackerFaction"]?.toIntOrNull() ?: 7

        return ParsedSiege(
            defenderLandId = land,
            attackerArmyId = attackerArmy,
            startedTurn = startedTurn,
            attackerFactionIndex = attackerFaction
        )
    }

    override suspend fun knownGarrisons(actorId: Long): List<GarrisonKnowledge> {
        // Optional first cut: none
        return emptyList()
    }

    override suspend fun knownMovements(actorId: Long): List<MovementKnowledge> {
        val orders = moveDao.getActive()

        return orders
            .mapNotNull { mo: MovementOrderEntity ->
                val segs = pathDao.forOrder(mo.id).sortedBy { it.stepIndex }
                if (segs.isEmpty()) return@mapNotNull null

                MovementKnowledge(
                    armyId = mo.armyId,
                    fromLandId = segs.first().fromLandId,
                    toLandId = segs.last().toLandId,
                    createdTurn = mo.createdTurn,
                    path = buildList {
                        segs.forEach { s: PathSegmentEntity ->
                            if (isEmpty()) add(s.fromLandId)
                            add(s.toLandId)
                        }
                    }
                )
            }
    }
}