package com.example.mygame.engine_and_helpers.test_seeder

import androidx.room.withTransaction
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import com.example.mygame.database.AppDatabase
import com.example.mygame.database.economy_resources_trade.TreasuryEntity
import com.example.mygame.database.nobility_titles_and_court.NobleEntity
import com.example.mygame.database.world_and_geography.FortificationEntity
import com.example.mygame.database.world_and_geography.LandEntity
import com.example.mygame.database.world_and_geography.NeighborEntity
import com.example.mygame.database.world_and_geography.OwnershipEntity
import com.example.mygame.database.world_and_geography.RiverSegmentEntity
import com.example.mygame.database.world_and_geography.StructureEntity
import com.example.mygame.dao.persistence_and_game_state.ScenarioDao
import com.example.mygame.database.economy_resources_trade.CraftOrderEntity
import com.example.mygame.database.economy_resources_trade.FarmEntity
import com.example.mygame.database.economy_resources_trade.MarketEntity
import com.example.mygame.database.economy_resources_trade.MarketStockEntity
import com.example.mygame.database.economy_resources_trade.MintOrderEntity
import com.example.mygame.database.economy_resources_trade.PriceEntity
import com.example.mygame.database.economy_resources_trade.RecipeEntity
import com.example.mygame.database.economy_resources_trade.ResourceProductionEntity
import com.example.mygame.database.economy_resources_trade.ResourceStockEntity
import com.example.mygame.database.economy_resources_trade.StockEntity
import com.example.mygame.database.economy_resources_trade.WarehouseEntity
import com.example.mygame.database.economy_resources_trade.SawmillEntity
import com.example.mygame.database.economy_resources_trade.FisheryEntity
import com.example.mygame.database.economy_resources_trade.MineEntity
import com.example.mygame.database.economy_resources_trade.TaxCollectionEntity
import com.example.mygame.database.economy_resources_trade.TaxPolicyEntity
import com.example.mygame.database.economy_resources_trade.LossTimerEntity
import com.example.mygame.database.economy_resources_trade.TradeRouteEntity
import com.example.mygame.database.economy_resources_trade.ManifestLineEntity
import com.example.mygame.database.economy_resources_trade.TradeAgreementEntity
import com.example.mygame.database.economy_resources_trade.EmbargoEntity
import com.example.mygame.database.armies_units_warfare.ArmyEntity
import com.example.mygame.database.armies_units_warfare.UnitEntity
import com.example.mygame.database.armies_units_warfare.UnitCompositionEntity
import com.example.mygame.dao.armies_units_warfare.UnitCompositionDao
import com.example.mygame.database.armies_units_warfare.MoraleEntity
import com.example.mygame.database.armies_units_warfare.AmmoStockEntity
import com.example.mygame.database.armies_units_warfare.EquipmentStockEntity
import com.example.mygame.database.armies_units_warfare.BattleEntity
import com.example.mygame.database.armies_units_warfare.BattleParticipantEntity
import com.example.mygame.database.armies_units_warfare.BattleTurnEntity
import com.example.mygame.dao.armies_units_warfare.BattleTurnDao
import com.example.mygame.database.armies_units_warfare.SignalChannelEntity
import com.example.mygame.database.armies_units_warfare.LootEntity
import com.example.mygame.database.armies_units_warfare.SiegeEntity
import com.example.mygame.database.armies_units_warfare.TacticEntity
import com.example.mygame.database.armies_units_warfare.MilitaryOrderEntity
import com.example.mygame.database.armies_units_warfare.DesertionEventEntity
import com.example.mygame.database.rebellion_banditry.BanditGroupEntity
import com.example.mygame.database.rebellion_banditry.BanditArmyEntity
import com.example.mygame.database.rebellion_banditry.OutlawEntity
import com.example.mygame.database.justice_and_court.CourtEntity
import com.example.mygame.database.justice_and_court.CrimeEntity
import com.example.mygame.database.justice_and_court.TrialEntity
import com.example.mygame.database.justice_and_court.CaseEntity
import com.example.mygame.database.justice_and_court.VerdictEntity
import com.example.mygame.database.justice_and_court.*

import com.example.mygame.database.armies_units_warfare.*
import com.example.mygame.database.dignity_duels_conflicts.*
import com.example.mygame.database.economy_resources_trade.*
import com.example.mygame.database.entertainment_and_social.*
import com.example.mygame.database.foundations_core.*
import com.example.mygame.database.justice_and_court.*
import com.example.mygame.database.messaging_and_information.*
import com.example.mygame.database.movements_logistics_supplies.*
import com.example.mygame.database.nobility_titles_and_court.*
import com.example.mygame.database.persistence_and_game_state.*
import com.example.mygame.database.politics_diplomacy_succession.*
import com.example.mygame.database.population_and_society.*
import com.example.mygame.database.rebellion_banditry.*
import com.example.mygame.database.religion.*
import com.example.mygame.database.roles_and_offices.*
import com.example.mygame.database.world_and_geography.*
import com.example.mygame.dao.armies_units_warfare.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.mygame.database.Actor
import com.example.mygame.database.ActorLocation

/**
 * One pass:
 *  1) Insert base facts from SeedData (countries, lands, identities...)
 *  2) Read real IDs and build lookup maps
 *  3) Programmatically generate rows for EVERY table (2–4 each), consistent FKs
 */
@Singleton
class DbSeeder @Inject constructor(
    private val db: AppDatabase
) {
    suspend fun seedAllIfEmpty() = withContext(Dispatchers.IO) {
        db.withTransaction {
            if (db.CountryDao().countAll() > 0) return@withTransaction
            seedAllFromScratch()
        }
    }

    suspend fun resetForNewGame(selectedScenarioDescr: String): ScenarioEntity = withContext(Dispatchers.IO) {
        db.clearAllTables()

        db.withTransaction {
            resetAutoIncrementSequences()
            seedAllFromScratch()
        }

        db.scenarioDao().getAllScenarios()
            .firstOrNull { it.descr == selectedScenarioDescr }
            ?: error("Scenario '$selectedScenarioDescr' was not found after reseeding")
    }

    private suspend fun resetAutoIncrementSequences() {
        db.openHelper.writableDatabase.execSQL("DELETE FROM sqlite_sequence")
    }

    private suspend fun seedAllFromScratch() {
        val countryIds = insertCountries()
        val landIds = insertLands(countryIds)
        insertNeighbors(landIds)
        insertOwnerships(countryIds, landIds)
        val religionIds = insertReligions()
        val nationIds = insertNationalities()
        val nobleIds = insertNobles(nationIds, religionIds)
        insertTerrains()

        insertStructures(landIds)
        insertForts(landIds)
        val riverIds = insertRivers()
        insertRiverSegments(riverIds, landIds)

        insertScenarios(nobleIds, landIds)
        seedNobleActorsAndLocations(nobleIds, landIds)

        seedEconomy(countryIds, landIds)
        seedMilitary(countryIds, landIds, nobleIds)
        seedJustice(landIds, nobleIds)
        seedDignity(nobleIds)
        seedMessaging(nobleIds, landIds)
        seedReligion(landIds, religionIds, nobleIds)
        seedSociety(landIds, nobleIds)
        seedBallInviteeFixtures(landIds, nobleIds)
        seedLogistics(landIds)
        seedWorldDynamics(landIds, riverIds)

        seedBallStartFixtures(landIds, nobleIds)

        seedMeta(turn = 1)
    }

    // ---------------- Base inserts from SeedData ----------------

    private suspend fun seedBallInviteeFixtures(landIds: List<Long>, nobleIds: List<Long>) {
        if (landIds.size < 3 || nobleIds.size < 4) return

        val organizerId = nobleIds[0]
        val localBallLand = landIds[0]
        val noLocalGuestsBallLand = landIds[1]
        val remoteGuestLand = landIds[2]

        val noblesById = db.NobleDao().getAll().associateBy { it.id }

        db.OwnershipDao().upsert(
            OwnershipEntity(
                landId = localBallLand,
                ownerType = "NOBLE",
                ownerRef = organizerId
            )
        )
        db.OwnershipDao().upsert(
            OwnershipEntity(
                landId = noLocalGuestsBallLand,
                ownerType = "NOBLE",
                ownerRef = organizerId
            )
        )

        val localManorId = db.StructureDao().upsert(
            StructureEntity(
                id = 0,
                landId = localBallLand,
                structureType = "MANOR",
                structureGroup = "RESIDENCE",
                ownerType = "NOBLE",
                ownerRef = organizerId
            )
        )

        val secondVenueId = db.StructureDao().upsert(
            StructureEntity(
                id = 0,
                landId = noLocalGuestsBallLand,
                structureType = "PALACE",
                structureGroup = "RESIDENCE",
                ownerType = "NOBLE",
                ownerRef = organizerId
            )
        )

        val ballGuestTitleId = db.TitleDao().upsert(
            TitleEntity(
                id = 0,
                name = "Ball Guest Title",
                sequenced = true
            )
        )

        db.NobleTitleDao().upsert(
            NobleTitleEntity(
                nobleId = nobleIds[1],
                titleId = ballGuestTitleId
            )
        )
        db.NobleTitleDao().upsert(
            NobleTitleEntity(
                nobleId = nobleIds[2],
                titleId = ballGuestTitleId
            )
        )
        db.NobleTitleDao().upsert(
            NobleTitleEntity(
                nobleId = nobleIds[3],
                titleId = ballGuestTitleId
            )
        )

        val knowledgeMsgId = db.MessageDao().upsert(
            MessageEntity(
                id = 0,
                fromActorId = organizerId,
                toActorId = organizerId,
                toRole = null,
                type = MessageType.LETTER,
                stampBroken = false,
                sentTurn = 1,
                payload = "BALL_TEST_KNOWLEDGE"
            )
        )

        suspend fun rememberForOrganizer(fact: String, turn: Int = 1) {
            db.KnowledgeDao().upsert(
                KnowledgeEntryEntity(
                    id = 0,
                    actorId = organizerId,
                    fact = fact,
                    sourceMsgId = knowledgeMsgId,
                    turn = turn,
                    confirmed = true
                )
            )
        }

        rememberForOrganizer(
            "STRUCTURE:$localManorId:LAND=$localBallLand:TYPE=MANOR:NAME=Test Manor of Land $localBallLand"
        )
        rememberForOrganizer(
            "STRUCTURE_OWNER:$localManorId:TYPE=NOBLE:REF=$organizerId"
        )

        rememberForOrganizer(
            "STRUCTURE:$secondVenueId:LAND=$noLocalGuestsBallLand:TYPE=PALACE:NAME=Test Palace of Land $noLocalGuestsBallLand"
        )
        rememberForOrganizer(
            "STRUCTURE_OWNER:$secondVenueId:TYPE=NOBLE:REF=$organizerId"
        )

        rememberForOrganizer("LAND_OWNER:$localBallLand:TYPE=NOBLE:REF=$organizerId")
        rememberForOrganizer("LAND_OWNER:$noLocalGuestsBallLand:TYPE=NOBLE:REF=$organizerId")

        val localNobleOneName = noblesById[nobleIds[1]]?.name ?: "Noble ${nobleIds[1]}"
        val localNobleTwoName = noblesById[nobleIds[2]]?.name ?: "Noble ${nobleIds[2]}"
        val remoteNobleName = noblesById[nobleIds[3]]?.name ?: "Noble ${nobleIds[3]}"

        rememberForOrganizer("ACTOR_NAME:${nobleIds[1]}:$localNobleOneName")
        rememberForOrganizer("ACTOR_AT:${nobleIds[1]}:LAND=$localBallLand:IMPRISONED=0")

        rememberForOrganizer("ACTOR_NAME:${nobleIds[2]}:$localNobleTwoName")
        rememberForOrganizer("ACTOR_AT:${nobleIds[2]}:LAND=$localBallLand:IMPRISONED=0")

        rememberForOrganizer("ACTOR_NAME:${nobleIds[3]}:$remoteNobleName")
        rememberForOrganizer("ACTOR_AT:${nobleIds[3]}:LAND=$remoteGuestLand:IMPRISONED=0")

        rememberForOrganizer("RESOURCE_AT:$localBallLand:ITEM=WINE:QTY=8")
        rememberForOrganizer("MARKET_RESOURCE:$localBallLand:ITEM=WINE:QTY=20:PRICE=3")
        rememberForOrganizer("MARKET_RESOURCE:$noLocalGuestsBallLand:ITEM=WINE:QTY=12:PRICE=4")

        val scenarios = db.scenarioDao().getAllScenarios()
        val scenarioIds = scenarios.map { it.scenarioId }.ifEmpty { listOf(1L) }

        val seededBards = listOf(
            Triple("Local Bard One", localBallLand, 1),
            Triple("Famous Bard Two", remoteGuestLand, 3),
            Triple("Court Bard Three", noLocalGuestsBallLand, 2)
        )

        val bardBaseActorId = 50_000L

        scenarioIds.forEachIndexed { scenarioIndex, scenarioId ->
            seededBards.forEachIndexed { bardIndex, (bardName, bardLandId, notabilityLevel) ->
                val bardActorId = bardBaseActorId + (scenarioIndex * 100) + bardIndex + 1L

                db.actorDAO().upsert(
                    Actor(
                        scenarioId = scenarioId,
                        actorId = bardActorId,
                        name = bardName,
                        actorType = "BARD",
                        notabilityLevel = notabilityLevel
                    )
                )

                db.actorLocationDAO().upsert(
                    ActorLocation(
                        scenarioId = scenarioId,
                        actorId = bardActorId,
                        locationId = bardLandId
                    )
                )

                if (scenarioIndex == 0) {
                    rememberForOrganizer("ACTOR_NAME:$bardActorId:$bardName")
                    rememberForOrganizer("ACTOR_AT:$bardActorId:LAND=$bardLandId:IMPRISONED=0")
                }
            }
        }
    }

    private suspend fun insertCountries(): List<Long> {

        val ids = mutableListOf<Long>()
        SeedData.countries.forEach { ids += db.CountryDao().upsert(it) }
        return ids
    }

    private suspend fun insertLands(countryIds: List<Long>): List<Long> {
        val ids = mutableListOf<Long>()
        SeedData.landProtos.forEach { p ->
            val countryId = countryIds[p.countryIndex]
            ids += db.LandDao().upsert(
                LandEntity(id = 0, name = p.name, countryId = countryId, terrain = p.terrain)
            )
        }
        return ids
    }

    private suspend fun insertNeighbors(landIds: List<Long>) {
        // make symmetric
        val pairs = SeedData.neighborPairs.flatMap { (a,b) -> listOf(a to b, b to a) }.distinct()
        pairs.forEach { (a,b) ->
            db.NeighborDao().upsert(NeighborEntity(landId = landIds[a], neighborId = landIds[b]))
        }
    }

    private suspend fun insertOwnerships(countryIds: List<Long>, landIds: List<Long>) {
        SeedData.landOwnershipByCountryIndex.forEach { (landIdx, countryIdx) ->
            db.OwnershipDao().upsert(
                OwnershipEntity(landId = landIds[landIdx], ownerType = "COUNTRY", ownerRef = countryIds[countryIdx])
            )
        }
    }

    private suspend fun insertReligions(): List<Long> {
        val ids = mutableListOf<Long>()
        SeedData.religions.forEach { ids += db.ReligionDao().upsert(it) }
        return ids
    }

    private suspend fun insertNationalities(): List<Long> {
        val ids = mutableListOf<Long>()
        SeedData.nationalities.forEach { ids += db.NationalityDao().upsert(it) }
        return ids
    }

    private suspend fun insertNobles(natIds: List<Long>, relIds: List<Long>): List<Long> {
        val ids = mutableListOf<Long>()
        SeedData.nobles.forEach { p ->
            ids += db.NobleDao().upsert(
                NobleEntity(id = 0,
                    name = p.name,
                    nationalityId = natIds[p.nationalityIndex],
                    religionId = relIds[p.religionIndex]
                )
            )
        }
        return ids
    }

    private suspend fun insertTerrains() {
        SeedData.terrains.forEach { db.TerrainDao().upsert(it) }
    }

    private suspend fun insertStructures(landIds: List<Long>) {
        SeedData.structures.forEach { s ->
            val landId = landIds[s.landIndex]
            val landOwner = db.OwnershipDao().ownerOf(landId)

            db.StructureDao().upsert(
                StructureEntity(
                    id = 0,
                    landId = landId,
                    structureType = s.type,
                    structureGroup = s.group,
                    ownerType = landOwner?.ownerType,
                    ownerRef = landOwner?.ownerRef
                )
            )
        }
    }

    private suspend fun insertForts(landIds: List<Long>) {
        SeedData.forts.forEach { f ->
            db.FortificationDao().upsert(
                FortificationEntity(id = 0, landId = landIds[f.landIndex], level = f.level, breached = f.breached)
            )
        }
    }

    private suspend fun insertRivers(): List<Long> {
        val ids = mutableListOf<Long>()
        SeedData.rivers.forEach { ids += db.RiverDao().upsert(it) }
        return ids
    }

    private suspend fun insertRiverSegments(riverIds: List<Long>, landIds: List<Long>) {
        SeedData.riverSegments.forEach { seg ->
            db.RiverSegmentDao().upsert(
                RiverSegmentEntity(riverId = riverIds[seg.riverIndex], orderIndex = seg.orderIndex, landId = landIds[seg.landIndex])
            )
        }
    }

    private suspend fun insertScenarios(nobleIds: List<Long>, landIds: List<Long>) {
        // Remap actorId/currentLandId from 1-based examples to our actual lists
        val a = nobleIds.first()
        val b = nobleIds.getOrNull(2) ?: nobleIds.last()
        val l1 = landIds.first()
        val l3 = landIds.getOrNull(2) ?: landIds.last()
        db.scenarioDao().insertScenario(
            SeedData.scenarios[0].copy(scenarioId = 0, actorId = a, currentLandId = l1)
        )
        db.scenarioDao().insertScenario(
            SeedData.scenarios[1].copy(scenarioId = 0, actorId = b, currentLandId = l3)
        )
    }

    // ---------------- Derivations so EVERY table gets rows ----------------

    private suspend fun seedNobleActorsAndLocations(
        nobleIds: List<Long>,
        landIds: List<Long>
    ) {
        if (nobleIds.isEmpty() || landIds.isEmpty()) return

        val nobles = db.NobleDao().getAll().associateBy { it.id }
        val scenarios = db.scenarioDao().getAllScenarios()
        if (scenarios.isEmpty()) return

        scenarios.forEach { scenario ->
            nobleIds.forEachIndexed { index, nobleId ->
                val noble = nobles[nobleId] ?: return@forEachIndexed

                val locationId = when (index) {
                    0 -> landIds.getOrElse(0) { landIds.first() }
                    1 -> landIds.getOrElse(1) { landIds.first() }
                    2 -> landIds.getOrElse(2) { landIds.last() }
                    3 -> landIds.getOrElse(3) { landIds.last() }
                    else -> landIds[index % landIds.size]
                }

                db.actorDAO().upsert(
                    Actor(
                        scenarioId = scenario.scenarioId,
                        actorId = nobleId,
                        name = noble.name,
                        actorType = "NOBLE",
                        notabilityLevel = 1
                    )
                )

                db.actorLocationDAO().upsert(
                    ActorLocation(
                        scenarioId = scenario.scenarioId,
                        actorId = nobleId,
                        locationId = locationId
                    )
                )
            }
        }
    }

    private suspend fun seedEconomy(countryIds: List<Long>, landIds: List<Long>) {
        // markets per land
        val marketIds = landIds.map { landId -> db.MarketDao().upsert(MarketEntity(id = 0, landId = landId, periodic = false)) }

        // stock & price for few items in each market
        val items = listOf("WOOD", "STONE", "IRON", "GRAIN")
        marketIds.forEachIndexed { idx, mId ->
            items.shuffled().take(2).forEach { item ->
                db.MarketStockDao().upsert(MarketStockEntity(marketId = mId, itemId = item, qty = 50 + idx * 10))
                db.PriceDao().upsert(PriceEntity(marketId = mId, itemId = item, price = 10 + idx))
            }
        }

        // treasury for each country
        countryIds.forEachIndexed { i, cid ->
            db.TreasuryDao().upsert(TreasuryEntity(id = 0, ownerType = "COUNTRY", ownerRef = cid, silverCoins = 1000 + i*500, goldCoins = 100 + i*20))
        }

        // warehouses + stock
        val whIds = landIds.map { l -> db.WarehouseDao().upsert(WarehouseEntity(id = 0, landId = l, capacity = 1000)) }
        whIds.forEachIndexed { i, w ->
            db.StockDao().upsert(StockEntity(warehouseId = w, itemId = "WOOD", qty = 100 + i*10, volume = 1))
            db.StockDao().upsert(StockEntity(warehouseId = w, itemId = "STONE", qty = 80 + i*8,  volume = 1))
        }

        // production & resource stock
        landIds.forEachIndexed { i, l ->
            db.ResourceProductionDao().upsert(ResourceProductionEntity(id = 0, landId = l, itemId = "WOOD",  effortPerTurn = 5+i))
            db.ResourceProductionDao().upsert(ResourceProductionEntity(id = 0, landId = l, itemId = "GRAIN", effortPerTurn = 3+i))
            db.ResourceStockDao().upsert(ResourceStockEntity(id = 0, landId = l, itemId = "WOOD",  qty = 50 + i*5))
            db.ResourceStockDao().upsert(ResourceStockEntity(id = 0, landId = l, itemId = "GRAIN", qty = 40 + i*4))
        }

        // craft/mint examples
        landIds.take(2).forEachIndexed { i, l ->
            val recipeId = db.RecipeDao().upsert(RecipeEntity(id = 0, outputItem = "BREAD", outputQty = 10, inputJson = """{"GRAIN":2}""", profession = "BAKER"))
            db.CraftOrderDao().upsert(CraftOrderEntity(id = 0, landId = l, recipeId = recipeId, assignedWorkers = 3+i))
            db.MintOrderDao().upsert(MintOrderEntity(id = 0, turn = 1+i, metal = "SILVER", pieces = 100+i*20))
        }

        // farms/fishery/sawmill/mines
        landIds.forEach { l ->
            db.FarmDao().upsert(FarmEntity(id = 0, landId = l, type = "GRAIN"))
            db.SawmillDao().upsert(SawmillEntity(id = 0, landId = l))
        }
        db.FisheryDao().upsert(FisheryEntity(id = 0, landId = landIds.last()))
        db.MineDao().upsert(MineEntity(id = 0, landId = landIds[2], resource = "IRON", remaining = 500))

        // tax policy & collection
        landIds.forEachIndexed { i, l ->
            db.TaxPolicyDao().upsert(TaxPolicyEntity(id = 0, landId = l, rate = 10 + i))
            db.TaxCollectionDao().upsert(TaxCollectionEntity(id = 0, landId = l, turn = 1, silverCollected = 100+i*10, goldCollected = 10+i))
        }

        // prices/loss timers for decay examples
        landIds.forEach { l ->
            db.LossTimerDao().upsert(LossTimerEntity(id = 0, landId = l, itemId = "GRAIN", nextLossTurn = 3))
        }

        // trade
        val routeA = db.TradeRouteDao().upsert(TradeRouteEntity(id = 0, name = "Northern Loop", originId = landIds.first()))
        db.ManifestDao().upsert(ManifestLineEntity(id = 0, routeId = routeA, marketId = marketIds.first(), itemId = "WOOD", action = "BUY",  qty = 20))
        db.ManifestDao().upsert(ManifestLineEntity(id = 0, routeId = routeA, marketId = marketIds.last(),  itemId = "WOOD", action = "SELL", qty = 20))
        db.TradeAgreementDao().upsert(TradeAgreementEntity(id = 0, countryA = countryIds[0], countryB = countryIds[1], terms = "LOW_TARIFF"))
        db.EmbargoDao().upsert(EmbargoEntity(id = 0, countryA = countryIds[1], countryB = countryIds[0], active = false))
    }

    private suspend fun seedMilitary(countryIds: List<Long>, landIds: List<Long>, nobleIds: List<Long>) {
        // armies (2 per country)
        val a1 = db.ArmyDao().upsert(ArmyEntity(id = 0, name = "Ardania 1st", countryId = countryIds[0], landId = landIds[0], commanderActorId = nobleIds[0], morale = 70))
        val a2 = db.ArmyDao().upsert(ArmyEntity(id = 0, name = "Ardania 2nd", countryId = countryIds[0], landId = landIds[1], commanderActorId = nobleIds[1], morale = 65))
        val b1 = db.ArmyDao().upsert(ArmyEntity(id = 0, name = "Belloria 1st", countryId = countryIds[1], landId = landIds[2], commanderActorId = nobleIds[2], morale = 68))
        val b2 = db.ArmyDao().upsert(ArmyEntity(id = 0, name = "Belloria 2nd", countryId = countryIds[1], landId = landIds[3], commanderActorId = nobleIds[3], morale = 60))

        // units + composition + morale rows
        listOf(a1,a2,b1,b2).forEachIndexed { idx, armyId ->
            val u = db.UnitDao().upsert(UnitEntity(id = 0, armyId = armyId, type = "INF", mounted = false, armor = "LEATHER"))
            db.UnitCompositionDao().upsert(UnitCompositionEntity(id = 0, unitId = u, soldierType = "INFANTRY", count = 100 + idx*10))
            db.MoraleDao().upsert(MoraleEntity(unitId = u, value = 50 + idx*5))
        }

        // ammo/equipment owned by armies & countries
        db.AmmoStockDao().upsert(AmmoStockEntity(ownerType = "ARMY", ownerRef = a1, ammoType = "ARROWS", qty = 500))
        db.EquipmentStockDao().upsert(EquipmentStockEntity(ownerType = "COUNTRY", ownerRef = countryIds[0], itemId = "SPEAR", qty = 200))

        // battle at border
        val battleId = db.BattleDao().upsert(BattleEntity(id = 0, landId = landIds[1], turn = null, startedTurn = 1))
        db.BattleParticipantDao().upsert(BattleParticipantEntity(id = 0, battleId = battleId, armyId = a2, side = "A"))
        db.BattleParticipantDao().upsert(BattleParticipantEntity(id = 0, battleId = battleId, armyId = b1, side = "B"))
        db.BattleTurnDao().upsert(BattleTurnEntity(id = 0, battleId = battleId, turn = 1))
        db.SignalChannelDao().upsert(SignalChannelEntity(id = 0, battleId = battleId, type = "FLAG"))
        db.LootDao().upsert(LootEntity(id = 0, battleId = battleId, itemId = "COIN", qty = 30))

        // siege at a fort
        db.SiegeDao().upsert(SiegeEntity(id = 0, landId = landIds[2], attackerArmyId = a2, defenderArmyId = b1, startedTurn = 1))

        // tactics catalog
        db.TacticDao().upsert(TacticEntity(id = 0, code = "SHIELDWALL", terrain = "PLAINS"))
        db.TacticDao().upsert(TacticEntity(id = 0, code = "AMBUSH",     terrain = "FOREST"))

        // orders & desertion log
        db.MilitaryOrderDao().upsert(MilitaryOrderEntity(id = 0, armyId = a1, type = "MOVE", payload = "TO=${landIds[1]}", issuedTurn = 1))
        db.DesertionDao().upsert(DesertionEventEntity(id = 0, unitId = 1, turn = 2, count = 3))

        // rebel/bandit examples
        val bandGrp = db.BanditGroupDao().upsert(BanditGroupEntity(id = 0, landId = landIds[2], notoriety = 5))
        db.BanditArmyDao().upsert(BanditArmyEntity(id = 0, landId = landIds[2], strength = 40))
        db.OutlawDao().upsert(OutlawEntity(id = 0, banditGroupId = bandGrp, name = "Black Rolf"))
    }

    private suspend fun seedJustice(landIds: List<Long>, nobleIds: List<Long>) {
        val courtId = db.CourtDao().upsert(CourtEntity(id = 0, type = "CIVIL", landId = landIds[0]))
        val crimeId = db.CrimeDao().upsert(CrimeEntity(id = 0, landId = landIds[0], type = "THEFT", reportedBy = nobleIds[0], details = "Stolen grain", turn = 1))
        db.TrialDao().upsert(TrialEntity(id = 0, crimeId = crimeId, judgeNobleId = nobleIds[1], startedTurn = 1))
        val caseId = db.CaseDao().upsert(CaseEntity(id = 0, courtId = courtId, accusedId = nobleIds[2], crime = "INSULT", turnOpened = 1))
        val verdictId = db.VerdictDao().upsert(VerdictEntity(Id = 0, caseId = caseId, guilty = false, turn = 2))
        db.PunishmentDao().upsert(PunishmentEntity(id = 0, verdictId = verdictId, type = "FINE"))
        db.EvidenceDao().upsert(EvidenceEntity(id = 0, caseId = caseId, strength = 2, description = "Weak testimony"))
        db.OffenseDao().upsert(OffenseEntity(id = 0, offenderId = nobleIds[2], victimId = nobleIds[0], turn = 1, details = "Public insult"))
        db.PersonalConflictDao().upsert(PersonalConflictEntity(id = 0, nobleA = nobleIds[0], nobleB = nobleIds[2], startedTurn = 1, state = "SOUR"))
    }

    private suspend fun seedDignity(nobleIds: List<Long>) {
        db.DuelBanDao().upsert(DuelBanEntity(countryId = 1, active = false))
        val duelId = db.DuelDao().upsert(DuelEntity(id = 0, participantA = nobleIds[0], participantB = nobleIds[2], turn = 2, outcome = "DRAW"))
        db.DuelEventDao().upsert(DuelEventEntity(id = 0, challengerId = nobleIds[0], challengedId = nobleIds[2], turn = 1, outcome = "REQUESTED"))
        db.HonorLogDao().upsert(HonorLogEntity(id = 0, nobleId = nobleIds[0], delta = +1, reason = "Kept word", turn = 1))
        db.PrestigeDao().upsert(PrestigeLogEntity(id = 0, nobleId = nobleIds[2], delta = -1, reason = "Duel avoided", turn = 2))
        db.RespectDao().upsert(RespectFearEntity(nobleId = nobleIds[0], respect = 55, fear = 20))
        db.FlagDao().upsert(FlagEntity(id = 0, unitId = 1, name = "Sun Banner", fearAura = 3))
        db.FavoriteDao().upsert(FavoriteFlag(nobleId = nobleIds[0], isFavorite = true))
        db.AssassinationDao().upsert(AssassinationContractEntity(id = 0, hirerId = nobleIds[3], targetId = nobleIds[1], status = "OPEN"))
    }

    private suspend fun seedMessaging(nobleIds: List<Long>, landIds: List<Long>) {
        landIds.forEach { landId ->
            db.PostOfficeDao().upsert(
                PostOfficeEntity(
                    landId = landId,
                    messengerCapacity = 5
                )
            )

            db.MessengerDao().upsert(
                MessengerEntity(
                    id = 0,
                    landId = landId,
                    loyal = true
                )
            )

            db.DoveDao().upsert(
                DoveEntity(
                    id = 0,
                    homeLandId = landId
                )
            )

            db.DoveDao().upsert(
                DoveEntity(
                    id = 0,
                    homeLandId = landId
                )
            )
        }

        val m1 = db.MessageDao().upsert(
            MessageEntity(
                id = 0,
                fromActorId = nobleIds[0],
                toActorId = nobleIds[2],
                toRole = null,
                type = MessageType.LETTER,
                stampBroken = false,
                sentTurn = 1,
                payload = "HELLO"
            )
        )
        db.SealDao().upsert(SealEntity(id = 0, messageId = m1, accuracy = 100))
        db.InterceptionDao().upsert(InterceptionLogEntity(id = 0, messageId = m1, turn = 1, details = "Clean"))
        db.KnowledgeDao().upsert(
            KnowledgeEntryEntity(
                id = 0,
                actorId = nobleIds[2],
                fact = "Greeting",
                sourceMsgId = m1,
                turn = 1,
                confirmed = true
            )
        )

        val m2 = db.MessageDao().upsert(
            MessageEntity(
                id = 0,
                fromActorId = nobleIds[1],
                toActorId = nobleIds[3],
                toRole = "CHANCELLOR",
                type = MessageType.ORAL,
                stampBroken = false,
                sentTurn = 2,
                payload = "REQUEST:REPORT"
            )
        )
        db.SealDao().upsert(SealEntity(id = 0, messageId = m2, accuracy = 90))
    }

    private suspend fun seedReligion(landIds: List<Long>, religionIds: List<Long>, nobleIds: List<Long>) {
        // ranks, priests, temples, monastery & monks
        val r0 = religionIds.first()
        val r1 = religionIds.last()
        val rank0 = db.RankDao().upsert(ReligionRankEntity(id = 0, religionId = r0, orderIndex = 0, title = "Acolyte"))
        val rank1 = db.RankDao().upsert(ReligionRankEntity(id = 0, religionId = r0, orderIndex = 1, title = "Priest"))
        db.PriestDao().upsert(PriestEntity(id = 0, religionId = r0, rankId = rank1, nobleRef = nobleIds[1]))
        db.TempleDao().upsert(TempleEntity(id = 0, landId = landIds[2], religionId = r0))
        val monId = db.MonasteryDao().upsert(MonasteryEntity(id = 0, landId = landIds[1], religionId = r0))
        db.MonkDao().upsert(MonkEntity(id = 0, monasteryId = monId, traveling = false))
        db.OppressionDao().upsert(OppressionStateEntity(landId = landIds[3], oppressedReligionId = r1, oppressedNationalityId = null))
        db.CelibacyRuleDao().upsert(CelibacyRuleEntity(religionId = r0, celibacy = false))
        db.ToleranceDao().upsert(ToleranceMatrixEntity(religionA = r0, religionB = r1, tolerant = true))
        db.LeaderDao().upsert(LeaderEntity(religionId = r0, priestId = nobleIds[1]))

        // conversion task & clashes
        db.ConversionDao().upsert(ConversionTaskEntity(id = 0, targetType = "LAND", targetRef = landIds[0], religionId = r0, progress = 10))
        db.ReligiousClashDao().upsert(ReligiousClashLogEntity(id = 0, landId = landIds[3], turn = 2, details = "Festival dispute"))
    }

    private suspend fun seedSociety(landIds: List<Long>, nobleIds: List<Long>) {
        val scenarios = db.scenarioDao().getAllScenarios()

        landIds.forEachIndexed { index, landId ->
            db.PopulationDao().upsert(
                PopulationStatEntity(
                    landId = landId,
                    total = 1000 + index * 150
                )
            )
            db.SatisfactionDao().upsert(
                SatisfactionEntity(
                    landId = landId,
                    level = 50 + index * 3
                )
            )
            db.HouseholdDao().upsert(
                HouseholdEntity(
                    id = 0,
                    landId = landId,
                    type = "URBAN",
                    tier = "MIDDLE"
                )
            )
            db.GuildDao().upsert(
                GuildEntity(
                    id = 0,
                    landId = landId,
                    profession = "CARPENTER"
                )
            )

            val mayorActorId = 70_000L + landId
            val mayorName = "Mayor of ${db.LandDao().get(landId)?.name ?: "Land $landId"}"

            db.MayorDao().upsert(
                MayorEntity(
                    landId = landId,
                    name = mayorName,
                    actorId = mayorActorId
                )
            )

            scenarios.forEach { scenario ->
                db.actorDAO().upsert(
                    Actor(
                        scenarioId = scenario.scenarioId,
                        actorId = mayorActorId,
                        name = mayorName,
                        actorType = "MAYOR",
                        notabilityLevel = 1
                    )
                )

                db.actorLocationDAO().upsert(
                    ActorLocation(
                        scenarioId = scenario.scenarioId,
                        actorId = mayorActorId,
                        locationId = landId
                    )
                )
            }
        }

        db.MayorOrderDao().upsert(
            MayorOrderEntity(
                id = 0,
                landId = landIds[0],
                type = "MAINTENANCE",
                payload = "Bridge repair"
            )
        )

        db.OfficeDao().upsert(
            OfficeEntity(
                id = 0,
                name = "Chancellor",
                description = "Head of admin"
            )
        )

        val ch = db.ChancellorDao().upsert(
            ChancellorEntity(
                id = 0,
                nobleId = nobleIds[1],
                rulerId = nobleIds[0]
            )
        )

        db.OfficeAssignmentDao().upsert(
            OfficeAssignmentEntity(
                id = 0,
                officeId = ch,
                nobleId = nobleIds[1],
                startTurn = 1,
                endTurn = null
            )
        )

        val defenseRoleId = db.RoleDao().upsert(
            RoleEntity(
                id = 0,
                name = "DEFENSE_COMMANDER"
            )
        )

        db.RoleAssignmentDao().upsert(
            RoleAssignmentEntity(
                id = 0,
                roleId = defenseRoleId,
                nobleId = nobleIds[3],
                startTurn = 1,
                endTurn = null
            )
        )

        val coinHolderRoleId = db.RoleDao().upsert(
            RoleEntity(
                id = 0,
                name = "COIN_HOLDER"
            )
        )

        db.RoleAssignmentDao().upsert(
            RoleAssignmentEntity(
                id = 0,
                roleId = coinHolderRoleId,
                nobleId = nobleIds[1],
                startTurn = 1,
                endTurn = null
            )
        )

        val titleId = db.TitleDao().upsert(
            TitleEntity(
                id = 0,
                name = "Duke",
                sequenced = true
            )
        )

        db.TitleTrackDao().upsert(
            TitleTrackEntity(
                id = 0,
                titleId = titleId,
                nextTitleId = null
            )
        )
        db.NobleTitleDao().upsert(NobleTitleEntity(nobleId = nobleIds[3], titleId = titleId))
        db.TraitDao().upsert(TraitEntity(id = 0, nobleId = nobleIds[0], key = "BRAVE", value = 2))
        db.FamilyDao().upsert(FamilyLinkEntity(a = nobleIds[0], b = nobleIds[1], relation = "BROTHERS"))
        db.CourtPositionDao().upsert(CourtPositionEntity(id = 0, nobleId = nobleIds[2], type = "ADVISOR"))
        db.CourtMembershipDao().upsert(CourtMembershipEntity(id = 0, nobleId = nobleIds[2], rulerId = nobleIds[3]))
        db.CourtExpenseDao().upsert(CourtExpenseEntity(id = 0, nobleId = nobleIds[3], type = "BANQUET", amount = 120))
    }

    private suspend fun seedLogistics(landIds: List<Long>) {
        // crossings, dams, boats/ships/convoys/wagons, depots, supply lines, hazards, water sources
        landIds.forEachIndexed { i, l ->
            db.CrossingDao().upsert(CrossingEntity(id = 0, landId = l, type = "FORD", capacity = 10 + i))
            db.WaterSourceDao().upsert(WaterSourceEntity(id = 0, landId = l, type = "WELL"))
            db.HazardDao().upsert(HazardEntity(id = 0, landId = l, type = "WOLVES"))
            db.SupplyDepotDao().upsert(SupplyDepotEntity(id = 0, landId = l, capacity = 100 + i*20))
        }
        val boatId = db.BoatDao().upsert(BoatEntity(id = 0, landId = landIds[3], capacity = 20))
        val shipId = db.ShipDao().upsert(ShipEntity(id = 0, portLandId = landIds[3], capacity = 200))
        val convoyId = db.ConvoyDao().upsert(ConvoyEntity(id = 0, armyId = null, landId = landIds[0]))
        db.WagonDao().upsert(WagonEntity(id = 0, convoyId = convoyId, capacity = 10))

        // movement orders + path
        val mo = db.MovementOrderDao().upsert(MovementOrderEntity(id = 0, armyId = 1, createdTurn = 1, status = "NEW"))
        db.PathSegmentDao().upsert(PathSegmentEntity(id = 0, movementOrderId = mo, stepIndex = 0, fromLandId = landIds[0], toLandId = landIds[1]))

        // ration plans & fatigue & morale hit
        db.RationPlanDao().upsert(RationPlanEntity(id = 0, subjectId = 1, foodTurns = 2, waterTurns = 2))
        db.FatigueStateDao().upsert(FatigueStateEntity(subjectId = 1, level = "TIRED", lastRestTurn = 1))
        db.MoraleHitDao().upsert(MoraleHitEntity(id = 0, unitId = 1, reason = "Starved", delta = -2))

        // requisitions & logistics logs
        db.RequisitionDao().upsert(RequisitionEventEntity(id = 0, landId = landIds[1], armyId = 1, deltaSatisfaction = -3))
        db.LogisticsLogDao().upsert(LogisticsLogEntity(id = 0, turn = 1, armyId = 1, details = "Convoy delayed"))
        db.SecretRouteDao().upsert(SecretRouteEntity(id = 0, fromLandId = landIds[0], toLandId = landIds[2], risk = 30))
        db.RouteDao().upsert(RouteEntity(id = 0, name = "Hill Pass"))
        db.RouteStepDao().upsert(RouteStepEntity(routeId = 1, stepIndex = 0, landId = landIds[0]))
    }

    private suspend fun seedWorldDynamics(landIds: List<Long>, riverIds: List<Long>) {
        // flood, poison water, siegeworks, structure progress/effects
        db.FloodDao().upsert(FloodStateEntity(landId = landIds[2], floodTurns = 1))
        db.WaterPoisonDao().upsert(WaterPoisonStateEntity(landId = landIds[1], poisonedUntilTurn = 2))
        db.SiegeworkDao().upsert(SiegeworkEntity(id = 0, landId = landIds[2], type = "TOWER", progress = 10))
        val s = db.StructureDao().getAll().firstOrNull()?.id ?: return
        db.StructureProgressDao().upsert(StructureProgressEntity(structureId = s, totalEffort = 100, doneEffort = 25))
        db.StructureEffectDao().upsert(StructureEffectEntity(id = 0, structureType = "MARKET", effectCode = "TRADE_BONUS"))

        // civic events: festivals/balls/hunt/tournament/gossip/performance/poison attempt
        db.FestivalDao().upsert(FestivalEntity(id = 0, landId = landIds[0], turn = 1, theme = "Harvest"))
        db.FestivalEventDao().upsert(FestivalEventEntity(id = 0, landId = landIds[0], turn = 1, religious = false))

        db.HuntEventDao().upsert(HuntEventEntity(id = 0, landId = landIds[2], turn = 1))
        val festId = db.FestivalDao().upsert(FestivalEntity(id = 0, landId = landIds[3], turn = 2, theme = "Sea Blessing"))
        db.PerformanceDao().upsert(PerformanceEntity(id = 0, festivalId = festId, troupe = "Bards of Bay", rating = 4))
        db.PoisonAttemptDao().upsert(PoisonAttemptEntity(id = 0, eventType = "HUNT", eventId = 1, success = false))
        db.GossipDao().upsert(GossipEntity(id = 0, landId = landIds[0], text = "Merchant scandal", turn = 2))

        // diplomacy
        db.AmbassadorDao().upsert(AmbassadorEntity(id = 0, countryId = 1, landId = landIds[3], imprisoned = false))
        db.DiplomacyDao().upsert(DiplomaticStatusEntity(a = 1, b = 2, status = "PEACE"))
        db.CasusBelliDao().upsert(CasusBelliEntity(id = 0, countryA = 2, countryB = 1, reason = "Tariffs", turn = 2))
        db.SuccessionDao().upsert(SuccessionLinkEntity(id = 0, rulerId = 4, heirId = 3))
        db.MarriageDao().upsert(MarriageProposalEntity(id = 0, fromNobleId = 1, toNobleId = 3, status = "PENDING"))
        db.RevoltDao().upsert(RevoltEntity(id = 0, goal = "Tax Relief", startedLandId = landIds[1]))
        val rebId = db.RebellionDao().upsert(RebellionEntity(id = 0, landId = landIds[1], cause = "Oppression", startedTurn = 2))
        db.RebelArmyDao().upsert(RebelArmyEntity(id = 0, rebellionId = rebId, armyId = 1, role = "MAIN", createdTurn = 2, disbandedTurn = null))
        db.SpreadEventDao().upsert(SpreadEventEntity(id = 0, revoltId = 1, landId = landIds[2], turn = 3))
        db.SuppressionLogDao().upsert(SuppressionLogEntity(id = 0, turn = 3, landId = landIds[2], details = "Militia restored order"))
    }

    private suspend fun seedMeta(turn: Int) {
        // clock + audit/integrity + rule overrides
        db.TurnClockDao().upsert(TurnClockEntity(id = 1, turn = turn, isNight = false, season = "SPRING", seed = 42L))
        db.AuditLogDao().upsert(AuditLogEntity(id = 0, turn = turn, actorId = null, action = "SEED", payloadJson = "{}", hash = "h1", createdAt = System.currentTimeMillis()))
        db.IntegrityDao().upsert(IntegrityViolationEntity(id = 0, turn = turn, code = "NONE", details = "ok", createdAt = System.currentTimeMillis()))
        db.RuleOverrideDao().upsert(RuleOverrideEntity(id = 0, key = "START_TAX", value = "10", scenarioId = 1))
        db.BudgetRequestDao().upsert(BudgetRequestEntity(id = 0, office = "DEFENSE", requesterId = 1, amount = 200, reason = "Barracks", turn = turn))
    }

    private suspend fun seedBallStartFixtures(
        landIds: List<Long>,
        nobleIds: List<Long>
    ) {
        if (landIds.isEmpty() || nobleIds.size < 4) return

        val scenarios = db.scenarioDao().getAllScenarios()
        if (scenarios.size < 2) return

        val scenarioOne = scenarios[0]
        val scenarioTwo = scenarios[1]

        val scenarioOnePlayerActorId = nobleIds[0]
        val scenarioTwoPlayerActorId = nobleIds[2]
        val chancellorActorId = nobleIds[1]
        val bardActorId = nobleIds[3]
        val scenarioOneCoinHolderActorId = 60_001L
        val scenarioTwoCoinHolderActorId = 60_002L

        val scenarioOneHostLandId = landIds[0]
        val chancellorActualLandId = landIds.getOrElse(1) { scenarioOneHostLandId }
        val scenarioTwoHostLandId = landIds.getOrElse(2) { scenarioOneHostLandId }

        val uniqueKnownSourceLands = linkedSetOf<Long>().apply {
            add(chancellorActualLandId)
            landIds.drop(3).forEach { add(it) }
            landIds.forEach { add(it) }
            remove(scenarioOneHostLandId)
        }.toList()

        val extraRemoteLandId = uniqueKnownSourceLands.getOrElse(0) { scenarioTwoHostLandId }
        val thirdBudgetSourceLandId = uniqueKnownSourceLands.getOrElse(1) { scenarioTwoHostLandId }

        db.actorLocationDAO().upsert(
            ActorLocation(
                scenarioId = scenarioOne.scenarioId,
                actorId = scenarioOnePlayerActorId,
                locationId = scenarioOneHostLandId
            )
        )
        db.actorLocationDAO().upsert(
            ActorLocation(
                scenarioId = scenarioOne.scenarioId,
                actorId = chancellorActorId,
                locationId = chancellorActualLandId
            )
        )
        db.actorLocationDAO().upsert(
            ActorLocation(
                scenarioId = scenarioTwo.scenarioId,
                actorId = scenarioTwoPlayerActorId,
                locationId = scenarioTwoHostLandId
            )
        )
        db.actorLocationDAO().upsert(
            ActorLocation(
                scenarioId = scenarioTwo.scenarioId,
                actorId = chancellorActorId,
                locationId = chancellorActualLandId
            )
        )
        db.actorLocationDAO().upsert(
            ActorLocation(
                scenarioId = scenarioOne.scenarioId,
                actorId = bardActorId,
                locationId = extraRemoteLandId
            )
        )
        db.actorLocationDAO().upsert(
            ActorLocation(
                scenarioId = scenarioTwo.scenarioId,
                actorId = bardActorId,
                locationId = extraRemoteLandId
            )
        )

        db.actorDAO().upsert(
            Actor(
                scenarioId = scenarioOne.scenarioId,
                actorId = scenarioOneCoinHolderActorId,
                name = "Osric the Coin Holder",
                actorType = "COIN_HOLDER",
                notabilityLevel = 1
            )
        )
        db.actorLocationDAO().upsert(
            ActorLocation(
                scenarioId = scenarioOne.scenarioId,
                actorId = scenarioOneCoinHolderActorId,
                locationId = scenarioOneHostLandId
            )
        )

        db.actorDAO().upsert(
            Actor(
                scenarioId = scenarioTwo.scenarioId,
                actorId = scenarioTwoCoinHolderActorId,
                name = "Mira the Coin Holder",
                actorType = "COIN_HOLDER",
                notabilityLevel = 1
            )
        )
        db.actorLocationDAO().upsert(
            ActorLocation(
                scenarioId = scenarioTwo.scenarioId,
                actorId = scenarioTwoCoinHolderActorId,
                locationId = scenarioTwoHostLandId
            )
        )

        val scenarioOneManorId = db.StructureDao().upsert(
            StructureEntity(
                id = 0,
                landId = scenarioOneHostLandId,
                structureType = "MANOR",
                structureGroup = "NOBILITY",
                ownerType = "NOBLE",
                ownerRef = scenarioOnePlayerActorId
            )
        )

        val scenarioOnePalaceId = db.StructureDao().upsert(
            StructureEntity(
                id = 0,
                landId = scenarioOneHostLandId,
                structureType = "PALACE",
                structureGroup = "NOBILITY",
                ownerType = "NOBLE",
                ownerRef = scenarioOnePlayerActorId
            )
        )

        val scenarioTwoManorId = db.StructureDao().upsert(
            StructureEntity(
                id = 0,
                landId = scenarioTwoHostLandId,
                structureType = "MANOR",
                structureGroup = "NOBILITY",
                ownerType = "NOBLE",
                ownerRef = scenarioTwoPlayerActorId
            )
        )

        val scenarioTwoPalaceId = db.StructureDao().upsert(
            StructureEntity(
                id = 0,
                landId = scenarioTwoHostLandId,
                structureType = "PALACE",
                structureGroup = "NOBILITY",
                ownerType = "NOBLE",
                ownerRef = scenarioTwoPlayerActorId
            )
        )

        db.BallDao().upsert(
            BallEventEntity(
                id = 0,
                landId = scenarioOneHostLandId,
                turn = 1,
                orderedByActorId = scenarioOnePlayerActorId,
                organizerActorId = scenarioOnePlayerActorId,
                venueStructureId = null,
                plannedBallId = 0L
            )
        )

        db.BallDao().upsert(
            BallEventEntity(
                id = 0,
                landId = scenarioTwoHostLandId,
                turn = 1,
                orderedByActorId = scenarioTwoPlayerActorId,
                organizerActorId = scenarioTwoPlayerActorId,
                venueStructureId = null,
                plannedBallId = 0L
            )
        )

        val scenarioOneMarket = db.MarketDao().inLand(scenarioOneHostLandId)
            ?: MarketEntity(
                id = db.MarketDao().upsert(
                    MarketEntity(id = 0, landId = scenarioOneHostLandId, periodic = false)
                ),
                landId = scenarioOneHostLandId,
                periodic = false
            )

        val scenarioTwoMarket = db.MarketDao().inLand(scenarioTwoHostLandId)
            ?: MarketEntity(
                id = db.MarketDao().upsert(
                    MarketEntity(id = 0, landId = scenarioTwoHostLandId, periodic = false)
                ),
                landId = scenarioTwoHostLandId,
                periodic = false
            )

        val chancellorLandMarket = db.MarketDao().inLand(chancellorActualLandId)
            ?: MarketEntity(
                id = db.MarketDao().upsert(
                    MarketEntity(id = 0, landId = chancellorActualLandId, periodic = false)
                ),
                landId = chancellorActualLandId,
                periodic = false
            )

        val remoteLandMarket = db.MarketDao().inLand(extraRemoteLandId)
            ?: MarketEntity(
                id = db.MarketDao().upsert(
                    MarketEntity(id = 0, landId = extraRemoteLandId, periodic = false)
                ),
                landId = extraRemoteLandId,
                periodic = false
            )

        val thirdSourceMarket = db.MarketDao().inLand(thirdBudgetSourceLandId)
            ?: MarketEntity(
                id = db.MarketDao().upsert(
                    MarketEntity(id = 0, landId = thirdBudgetSourceLandId, periodic = false)
                ),
                landId = thirdBudgetSourceLandId,
                periodic = false
            )

        db.ResourceStockDao().upsert(
            ResourceStockEntity(
                id = 0,
                landId = scenarioOneHostLandId,
                itemId = "WINE",
                qty = 18
            )
        )
        db.ResourceStockDao().upsert(
            ResourceStockEntity(
                id = 0,
                landId = chancellorActualLandId,
                itemId = "WINE",
                qty = 24
            )
        )
        if (extraRemoteLandId != scenarioOneHostLandId) {
            db.ResourceStockDao().upsert(
                ResourceStockEntity(
                    id = 0,
                    landId = extraRemoteLandId,
                    itemId = "WINE",
                    qty = 31
                )
            )
        }
        if (thirdBudgetSourceLandId != scenarioOneHostLandId &&
            thirdBudgetSourceLandId != extraRemoteLandId
        ) {
            db.ResourceStockDao().upsert(
                ResourceStockEntity(
                    id = 0,
                    landId = thirdBudgetSourceLandId,
                    itemId = "WINE",
                    qty = 27
                )
            )
        }
        db.ResourceStockDao().upsert(
            ResourceStockEntity(
                id = 0,
                landId = scenarioTwoHostLandId,
                itemId = "WINE",
                qty = 22
            )
        )

        db.TreasuryDao().upsert(
            TreasuryEntity(
                id = 0,
                ownerType = "LAND",
                ownerRef = scenarioOneHostLandId,
                silverCoins = 140,
                goldCoins = 0
            )
        )
        db.TreasuryDao().upsert(
            TreasuryEntity(
                id = 0,
                ownerType = "LAND",
                ownerRef = chancellorActualLandId,
                silverCoins = 220,
                goldCoins = 0
            )
        )
        if (extraRemoteLandId != scenarioOneHostLandId) {
            db.TreasuryDao().upsert(
                TreasuryEntity(
                    id = 0,
                    ownerType = "LAND",
                    ownerRef = extraRemoteLandId,
                    silverCoins = 310,
                    goldCoins = 0
                )
            )
        }
        if (thirdBudgetSourceLandId != scenarioOneHostLandId &&
            thirdBudgetSourceLandId != extraRemoteLandId
        ) {
            db.TreasuryDao().upsert(
                TreasuryEntity(
                    id = 0,
                    ownerType = "LAND",
                    ownerRef = thirdBudgetSourceLandId,
                    silverCoins = 180,
                    goldCoins = 0
                )
            )
        }
        db.TreasuryDao().upsert(
            TreasuryEntity(
                id = 0,
                ownerType = "LAND",
                ownerRef = scenarioTwoHostLandId,
                silverCoins = 160,
                goldCoins = 0
            )
        )

        db.TreasuryDao().upsert(
            TreasuryEntity(
                id = 0,
                ownerType = "ACTOR",
                ownerRef = 1L,
                silverCoins = 1500,
                goldCoins = 50
            )
        )

        suspend fun ensureMayor(landId: Long) {
            val mayorActorId = 70_000L + landId
            val mayorName = "Mayor of ${db.LandDao().get(landId)?.name ?: "Land $landId"}"

            db.MayorDao().upsert(
                MayorEntity(
                    landId = landId,
                    name = mayorName,
                    actorId = mayorActorId
                )
            )

            listOf(scenarioOne, scenarioTwo).forEach { scenario ->
                db.actorDAO().upsert(
                    Actor(
                        scenarioId = scenario.scenarioId,
                        actorId = mayorActorId,
                        name = mayorName,
                        actorType = "MAYOR",
                        notabilityLevel = 1
                    )
                )

                db.actorLocationDAO().upsert(
                    ActorLocation(
                        scenarioId = scenario.scenarioId,
                        actorId = mayorActorId,
                        locationId = landId
                    )
                )
            }
        }

        ensureMayor(scenarioOneHostLandId)
        ensureMayor(chancellorActualLandId)
        ensureMayor(extraRemoteLandId)
        ensureMayor(thirdBudgetSourceLandId)
        ensureMayor(scenarioTwoHostLandId)

        db.MarketStockDao().upsert(
            MarketStockEntity(
                marketId = scenarioOneMarket.id,
                itemId = "WINE",
                qty = 40
            )
        )
        db.PriceDao().upsert(
            PriceEntity(
                marketId = scenarioOneMarket.id,
                itemId = "WINE",
                price = 12
            )
        )

        db.MarketStockDao().upsert(
            MarketStockEntity(
                marketId = scenarioTwoMarket.id,
                itemId = "WINE",
                qty = 35
            )
        )
        db.PriceDao().upsert(
            PriceEntity(
                marketId = scenarioTwoMarket.id,
                itemId = "WINE",
                price = 11
            )
        )

        db.MarketStockDao().upsert(
            MarketStockEntity(
                marketId = chancellorLandMarket.id,
                itemId = "WINE",
                qty = 28
            )
        )
        db.PriceDao().upsert(
            PriceEntity(
                marketId = chancellorLandMarket.id,
                itemId = "WINE",
                price = 10
            )
        )

        db.MarketStockDao().upsert(
            MarketStockEntity(
                marketId = remoteLandMarket.id,
                itemId = "WINE",
                qty = 32
            )
        )
        db.PriceDao().upsert(
            PriceEntity(
                marketId = remoteLandMarket.id,
                itemId = "WINE",
                price = 13
            )
        )

        if (thirdSourceMarket.id != remoteLandMarket.id) {
            db.MarketStockDao().upsert(
                MarketStockEntity(
                    marketId = thirdSourceMarket.id,
                    itemId = "WINE",
                    qty = 26
                )
            )
            db.PriceDao().upsert(
                PriceEntity(
                    marketId = thirdSourceMarket.id,
                    itemId = "WINE",
                    price = 9
                )
            )
        }

        val scenarioOneKnowledgeMsgId = db.MessageDao().upsert(
            MessageEntity(
                id = 0,
                fromActorId = scenarioOnePlayerActorId,
                toActorId = scenarioOnePlayerActorId,
                toRole = null,
                type = MessageType.LETTER,
                stampBroken = false,
                sentTurn = 1,
                payload = "BALL_TEST_SETUP_SCENARIO_1"
            )
        )

        val scenarioTwoKnowledgeMsgId = db.MessageDao().upsert(
            MessageEntity(
                id = 0,
                fromActorId = scenarioTwoPlayerActorId,
                toActorId = scenarioTwoPlayerActorId,
                toRole = null,
                type = MessageType.LETTER,
                stampBroken = false,
                sentTurn = 1,
                payload = "BALL_TEST_SETUP_SCENARIO_2"
            )
        )

        suspend fun rememberForActor(
            viewerActorId: Long,
            sourceMsgId: Long,
            fact: String,
            turn: Int = 1
        ) {
            db.KnowledgeDao().upsert(
                KnowledgeEntryEntity(
                    id = 0,
                    actorId = viewerActorId,
                    fact = fact,
                    sourceMsgId = sourceMsgId,
                    turn = turn,
                    confirmed = true
                )
            )
        }

        val scenarioOneFacts = buildList {
            add("ACTOR_NAME:$scenarioOnePlayerActorId:Varek of Eastmarch")
            add("ACTOR_AT:$scenarioOnePlayerActorId:LAND=$scenarioOneHostLandId:IMPRISONED=0")

            add("ACTOR_NAME:$chancellorActorId:Ardan the Just")
            add("ACTOR_AT:$chancellorActorId:LAND=$scenarioOneHostLandId:IMPRISONED=0")

            add("ROLE_HOLDER:CHANCELLOR:ACTOR=$chancellorActorId")
            add("COUNCIL_ROLE:CHANCELLOR:ACTOR=$chancellorActorId")

            add("ACTOR_NAME:$scenarioOneCoinHolderActorId:Osric the Coin Holder")
            add("ACTOR_ROLE:$scenarioOneCoinHolderActorId:COIN_HOLDER")
            add("ACTOR_AT:$scenarioOneCoinHolderActorId:LAND=$scenarioOneHostLandId:IMPRISONED=0")
            add("ROLE_HOLDER:COIN_HOLDER:ACTOR=$scenarioOneCoinHolderActorId")
            add("COUNCIL_ROLE:COIN_HOLDER:ACTOR=$scenarioOneCoinHolderActorId")

            listOf(
                scenarioOneHostLandId,
                chancellorActualLandId,
                extraRemoteLandId,
                thirdBudgetSourceLandId,
                scenarioTwoHostLandId
            ).distinct().forEach { mayorLandId ->
                val mayorActorId = 70_000L + mayorLandId
                val mayorName = "Mayor of ${db.LandDao().get(mayorLandId)?.name ?: "Land $mayorLandId"}"
                add("ACTOR_NAME:$mayorActorId:$mayorName")
                add("ACTOR_ROLE:$mayorActorId:MAYOR")
                add("ACTOR_AT:$mayorActorId:LAND=$mayorLandId:IMPRISONED=0")
            }

            add("ACTOR_NAME:$bardActorId:Duke Harol")
            add("ACTOR_ROLE:$bardActorId:BARD")
            add("ACTOR_AT:$bardActorId:LAND=$extraRemoteLandId:IMPRISONED=0")

            add("STRUCTURE:$scenarioOneManorId:LAND=$scenarioOneHostLandId:TYPE=MANOR:NAME=Eastmarch Manor")
            add("STRUCTURE_OWNER:$scenarioOneManorId:TYPE=NOBLE:REF=$scenarioOnePlayerActorId")

            add("STRUCTURE:$scenarioOnePalaceId:LAND=$scenarioOneHostLandId:TYPE=PALACE:NAME=Eastmarch Palace")
            add("STRUCTURE_OWNER:$scenarioOnePalaceId:TYPE=NOBLE:REF=$scenarioOnePlayerActorId")

            add("LAND_RESOURCE:$scenarioOneHostLandId:ITEM=WINE:QTY=18")
            add("LAND_RESOURCE:$chancellorActualLandId:ITEM=WINE:QTY=24")

            if (thirdBudgetSourceLandId != scenarioOneHostLandId &&
                thirdBudgetSourceLandId != extraRemoteLandId
            ) {
                add("LAND_RESOURCE:$thirdBudgetSourceLandId:ITEM=WINE:QTY=27")
            }

            add("LAND_RESOURCE:$scenarioOneHostLandId:ITEM=SILVER:QTY=140")
            add("LAND_RESOURCE:$chancellorActualLandId:ITEM=SILVER:QTY=220")
            if (extraRemoteLandId != scenarioOneHostLandId) {
                add("LAND_RESOURCE:$extraRemoteLandId:ITEM=SILVER:QTY=310")
            }
            if (thirdBudgetSourceLandId != scenarioOneHostLandId &&
                thirdBudgetSourceLandId != extraRemoteLandId
            ) {
                add("LAND_RESOURCE:$thirdBudgetSourceLandId:ITEM=SILVER:QTY=180")
            }

            add("MARKET_RESOURCE:$scenarioOneHostLandId:ITEM=WINE:QTY=40:PRICE=12")
            add("MARKET_RESOURCE:$chancellorActualLandId:ITEM=WINE:QTY=28:PRICE=10")
            add("MARKET_RESOURCE:$extraRemoteLandId:ITEM=WINE:QTY=32:PRICE=13")
            if (thirdSourceMarket.id != remoteLandMarket.id) {
                add("MARKET_RESOURCE:$thirdBudgetSourceLandId:ITEM=WINE:QTY=26:PRICE=9")
            }
        }

        scenarioOneFacts.forEach { fact ->
            rememberForActor(
                viewerActorId = scenarioOnePlayerActorId,
                sourceMsgId = scenarioOneKnowledgeMsgId,
                fact = fact
            )
        }

        val scenarioTwoFacts = buildList {
            add("ACTOR_NAME:$scenarioTwoPlayerActorId:Lady Serin")
            add("ACTOR_AT:$scenarioTwoPlayerActorId:LAND=$scenarioTwoHostLandId:IMPRISONED=0")

            add("ACTOR_NAME:$chancellorActorId:Ardan the Just")
            add("ACTOR_AT:$chancellorActorId:LAND=$chancellorActualLandId:IMPRISONED=0")

            add("ROLE_HOLDER:CHANCELLOR:ACTOR=$chancellorActorId")
            add("COUNCIL_ROLE:CHANCELLOR:ACTOR=$chancellorActorId")

            add("ACTOR_NAME:$scenarioTwoCoinHolderActorId:Mira the Coin Holder")
            add("ACTOR_ROLE:$scenarioTwoCoinHolderActorId:COIN_HOLDER")
            add("ACTOR_AT:$scenarioTwoCoinHolderActorId:LAND=$scenarioTwoHostLandId:IMPRISONED=0")
            add("ROLE_HOLDER:COIN_HOLDER:ACTOR=$scenarioTwoCoinHolderActorId")
            add("COUNCIL_ROLE:COIN_HOLDER:ACTOR=$scenarioTwoCoinHolderActorId")

            listOf(
                scenarioOneHostLandId,
                chancellorActualLandId,
                extraRemoteLandId,
                thirdBudgetSourceLandId,
                scenarioTwoHostLandId
            ).distinct().forEach { mayorLandId ->
                val mayorActorId = 70_000L + mayorLandId
                val mayorName = "Mayor of ${db.LandDao().get(mayorLandId)?.name ?: "Land $mayorLandId"}"
                add("ACTOR_NAME:$mayorActorId:$mayorName")
                add("ACTOR_ROLE:$mayorActorId:MAYOR")
                add("ACTOR_AT:$mayorActorId:LAND=$mayorLandId:IMPRISONED=0")
            }

            add("ACTOR_NAME:$bardActorId:Duke Harol")
            add("ACTOR_ROLE:$bardActorId:BARD")
            add("ACTOR_AT:$bardActorId:LAND=$extraRemoteLandId:IMPRISONED=0")

            add("STRUCTURE:$scenarioTwoManorId:LAND=$scenarioTwoHostLandId:TYPE=MANOR:NAME=Serin Manor")
            add("STRUCTURE_OWNER:$scenarioTwoManorId:TYPE=NOBLE:REF=$scenarioTwoPlayerActorId")

            add("STRUCTURE:$scenarioTwoPalaceId:LAND=$scenarioTwoHostLandId:TYPE=PALACE:NAME=Serin Palace")
            add("STRUCTURE_OWNER:$scenarioTwoPalaceId:TYPE=NOBLE:REF=$scenarioTwoPlayerActorId")

            add("LAND_RESOURCE:$scenarioTwoHostLandId:ITEM=WINE:QTY=22")
            add("LAND_RESOURCE:$chancellorActualLandId:ITEM=WINE:QTY=24")
            if (thirdBudgetSourceLandId != scenarioTwoHostLandId &&
                thirdBudgetSourceLandId != extraRemoteLandId
            ) {
                add("LAND_RESOURCE:$thirdBudgetSourceLandId:ITEM=WINE:QTY=27")
            }

            add("LAND_RESOURCE:$scenarioTwoHostLandId:ITEM=SILVER:QTY=160")
            add("LAND_RESOURCE:$chancellorActualLandId:ITEM=SILVER:QTY=220")
            if (extraRemoteLandId != scenarioTwoHostLandId) {
                add("LAND_RESOURCE:$extraRemoteLandId:ITEM=SILVER:QTY=310")
            }
            if (thirdBudgetSourceLandId != scenarioTwoHostLandId &&
                thirdBudgetSourceLandId != extraRemoteLandId
            ) {
                add("LAND_RESOURCE:$thirdBudgetSourceLandId:ITEM=SILVER:QTY=180")
            }

            add("MARKET_RESOURCE:$scenarioTwoHostLandId:ITEM=WINE:QTY=35:PRICE=11")
            add("MARKET_RESOURCE:$chancellorActualLandId:ITEM=WINE:QTY=28:PRICE=10")
            add("MARKET_RESOURCE:$extraRemoteLandId:ITEM=WINE:QTY=32:PRICE=13")
            if (thirdSourceMarket.id != remoteLandMarket.id) {
                add("MARKET_RESOURCE:$thirdBudgetSourceLandId:ITEM=WINE:QTY=26:PRICE=9")
            }
        }

        scenarioTwoFacts.forEach { fact ->
            rememberForActor(
                viewerActorId = scenarioTwoPlayerActorId,
                sourceMsgId = scenarioTwoKnowledgeMsgId,
                fact = fact
            )
        }
    }
}