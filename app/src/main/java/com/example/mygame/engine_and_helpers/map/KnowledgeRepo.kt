package com.example.mygame.engine_and_helpers.map

import com.example.mygame.database.messaging_and_information.ArmyKnowledge
import com.example.mygame.database.messaging_and_information.GarrisonKnowledge
import com.example.mygame.database.messaging_and_information.MovementKnowledge
import com.example.mygame.database.messaging_and_information.SiegeKnowledge

/**
 * Tiny abstraction over Knowledge DAO + movement orders, so the engine code
 * doesn’t depend on exact "fact string" encoding.
 */
interface KnowledgeRepo {
    /** Last true knowledge for this actor about armies’ current lands. */
    suspend fun lastKnownArmyLandByFaction(
        actorId: Long
    ): List<ArmyKnowledge>

    /** Known sieges: attacker faction, defender land. Multiple attackers allowed. */
    suspend fun knownSieges(actorId: Long): List<SiegeKnowledge>

    /** Known garrisons by land & faction (optional). */
    suspend fun knownGarrisons(actorId: Long): List<GarrisonKnowledge>

    /** Known movement orders with createdTurn & path. */
    suspend fun knownMovements(actorId: Long): List<MovementKnowledge>

    suspend fun knownArmiesForActor(
        actorId: Long
    ): List<ArmyKnowledge>
}