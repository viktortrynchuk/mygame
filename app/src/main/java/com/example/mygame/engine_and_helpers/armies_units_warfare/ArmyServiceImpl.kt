package com.example.mygame.engine_and_helpers.armies_units_warfare

import com.example.mygame.dao.armies_units_warfare.ArmyDao
import com.example.mygame.dao.armies_units_warfare.UnitDao
import com.example.mygame.database.armies_units_warfare.ArmyEntity
import com.example.mygame.database.armies_units_warfare.UnitEntity
import com.example.mygame.engine_and_helpers.Constants.defaultMorale

interface ArmyService {
    suspend fun army(id: Long): ArmyEntity?
    suspend fun createArmy(name: String, commanderId: Long, landId: Long): Long
    suspend fun unitsOf(armyId: Long): List<UnitEntity>
    suspend fun addUnit(armyId: Long, type: String, mounted: Boolean, armor: String?): Long

    // Optional convenience with full control over new columns (use if needed)
    suspend fun createArmy(
        name: String,
        landId: Long,
        countryId: Long? = null,
        commanderActorId: Long? = null,
        morale: Int = defaultMorale
    ): Long
}

class ArmyServiceImpl(
    private val armyDao: ArmyDao,
    private val unitDao: UnitDao
) : ArmyService {

    override suspend fun army(id: Long) = armyDao.get(id)

    /**
     * Keeps the old signature but fills the new columns:
     * - countryId = null (rebel/factionless by default; pass via the overload if needed)
     * - commanderActorId = commanderId (maps old param to new column)
     * - morale = 50 (sane default; tweak as you like)
     */
    override suspend fun createArmy(name: String, commanderId: Long, landId: Long): Long =
        armyDao.upsert(
            ArmyEntity(
                id = 0,
                name = name,
                countryId = null,             // default; use the overload below to set
                landId = landId,
                commanderActorId = commanderId,
                morale = defaultMorale
            )
        )

    /**
     * New overload that exposes every column of ArmyEntity (Variant E).
     */
    override suspend fun createArmy(
        name: String,
        landId: Long,
        countryId: Long?,
        commanderActorId: Long?,
        morale: Int
    ): Long = armyDao.upsert(
        ArmyEntity(
            id = 0,
            name = name,
            countryId = countryId,
            landId = landId,
            commanderActorId = commanderActorId,
            morale = morale
        )
    )

    override suspend fun unitsOf(armyId: Long) = unitDao.forArmy(armyId)

    override suspend fun addUnit(
        armyId: Long,
        type: String,
        mounted: Boolean,
        armor: String?
    ): Long = unitDao.upsert(
        UnitEntity(
            id = 0,
            armyId = armyId,
            type = type,
            mounted = if (mounted) true else false, // keep your existing schema
            armor = armor
        )
    )
}