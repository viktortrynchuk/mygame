package com.example.mygame.engine_and_helpers.ai

import com.example.mygame.engine_and_helpers.armies_units_warfare.ArmyService
import com.example.mygame.engine_and_helpers.armies_units_warfare.OrdersService
import com.example.mygame.engine_and_helpers.armies_units_warfare.WarfareService
import com.example.mygame.engine_and_helpers.economy_resources_trade.InventoryService
import com.example.mygame.engine_and_helpers.economy_resources_trade.MarketService
import com.example.mygame.engine_and_helpers.messaging_and_information.MessagingService
import com.example.mygame.engine_and_helpers.movements_logistics_supplies.LogisticsService
import com.example.mygame.engine_and_helpers.politics_diplomacy_succession.PoliticsService
import com.example.mygame.engine_and_helpers.population_and_society.PopulationService
import com.example.mygame.engine_and_helpers.population_and_society.RevoltRiskEstimator
import com.example.mygame.engine_and_helpers.rebellion_banditry.RebellionService
import com.example.mygame.engine_and_helpers.religion.ReligionService
import com.example.mygame.engine_and_helpers.world_and_geography.WorldRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Coordinates all sub‑agents for one country per turn. */
interface AIDirector {
    suspend fun takeTurn(countryId: Long, turn: Int): AITurnReport
}

class AIDirectorImpl(
    private val world: WorldRepository,
    private val politics: PoliticsService,
    private val religion: ReligionService,
    private val population: PopulationService,
    private val economy: InventoryService,
    private val market: MarketService,
    private val armies: ArmyService,
    private val orders: OrdersService,
    private val warfare: WarfareService,
    private val logistics: LogisticsService,
    private val messages: MessagingService,
    private val rebellions: RebellionService,
    private val revoltRisk: RevoltRiskEstimator,
    private val selector: TargetSelector,
    private val econHeuristics: EconomicHeuristics
) : AIDirector {
    override suspend fun takeTurn(countryId: Long, turn: Int): AITurnReport = withContext(
        Dispatchers.Default) {
        var sent = 0
        var issued = 0
        var started = 0
        var suppressed = 0

        val lands = world.landsByCountry(countryId)
        // 1) Internal stability pass
        for (land in lands) {
            val sat = population.satisfactionOf(land.id)?.level ?: 50
            val risk = revoltRisk.riskPercent(land.id)
            val activeRebellion = rebellions.rebellionIn(land.id)
            if (activeRebellion != null) {
                // simple heuristic: log suppression and create a rebel-hunting army order if one exists nearby
                val candidateArmy = armies.army(selector.closestArmyIdToLand(land.id))
                if (candidateArmy != null) {
                    logistics.log(turn, candidateArmy.id, "SUPPRESS_REBELS@${land.id}")
                    issued += 1
                    suppressed += 1
                }
            } else if (risk > 60 || sat < 30) {
                // Send kind letter to mayor or lower taxes via coin holder in your game later
                messages.sendOral(
                    fromActorId = countryId,
                    toActorId = null,
                    toRole = "MAYOR_${land.id}",
                    payload = "Keep peace; distribute bread from reserves.",
                    sentTurn = turn
                )
                sent += 1
            }
        }

        // 2) Economy pass: if crops low, propose trade
        val shortageLands = lands.filter { econHeuristics.isShortOnCrops(it.id, economy) }
        if (shortageLands.isNotEmpty()) {
            // Pick a neighbor country to negotiate trade with
            val neighbors = politics.countries().filter { it.id != countryId }
            if (neighbors.isNotEmpty()) {
                val partner = neighbors.random()
                val terms = "CROPS_FOR_SILVER_T${turn}"
                market.setAgreement(countryId, partner.id, terms)
                messages.sendLetter(countryId, toActorId = partner.id, toRole = "RULER", payload = "TRADE:$terms", sentTurn = turn)
                sent += 1
            }
        }

        // 3) Military pass: if hostile neighbor, concentrate one army on a border land
        val hostileCountry = politics.countries().firstOrNull { other ->
            other.id != countryId && (politics.diplomaticStatus(countryId, other.id)?.status == "WAR")
        }
        if (hostileCountry != null) {
            val border = selector.pickBorderLandAgainst(countryId, hostileCountry.id, world)
            if (border != null) {
                val army = selector.firstArmyIdForCountry(countryId)
                if (army != null) {
                    val orderId = logistics.createMovementOrder(army, createdTurn = turn)
                    logistics.attachPath(orderId, listOf(border.land.id))
                    orders.issueOrder(army, type = "MOVE", payload = "to=${border.land.id}", issuedTurn = turn)
                    issued += 2
                }
            }
        }

        // 4) Opportunistic attack if two friendly armies present on same hostile border land
        if (hostileCountry != null) {
            val target = selector.pickRaidTarget(countryId, hostileCountry.id, world)
            if (target != null) {
                warfare.startBattle(target.land.id, startedTurn = turn)
                started += 1
            }
        }

        AITurnReport(sent, issued, started, suppressed)
    }
}