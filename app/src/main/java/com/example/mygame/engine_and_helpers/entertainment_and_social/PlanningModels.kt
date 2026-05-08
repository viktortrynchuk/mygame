package com.example.mygame.engine_and_helpers.entertainment_and_social

import com.example.mygame.database.messaging_and_information.DeliveryMethod
import com.example.mygame.database.messaging_and_information.MessageType

data class BardCandidate(
    val actorId: Long,
    val name: String,
    val lastKnownLandId: Long?,
    val lastSeenTurn: Int?,
    val notabilityLevel: Int = 1
)

data class AttendeeCandidate(
    val actorId: Long,
    val name: String,
    val lastKnownLandId: Long?,
    val lastSeenTurn: Int?,
    val imprisoned: Boolean,
    val titleRank: Int = 0
)

data class AlcoholStockOption(
    val code: String,
    val availableQty: Int,
    val landId: Long
)

data class MarketAlcoholOption(
    val landId: Long,
    val code: String,
    val availableQty: Int,
    val unitPrice: Int
)

data class VenueOption(
    val structureId: Long,
    val landId: Long,
    val name: String,
    val type: String
)

data class StockUse(
    val landId: Long,
    val code: String,
    val quantity: Int,
    val deliveryMethod: DeliveryMethod = DeliveryMethod.MESSENGER,
    val sendLater: Boolean = false,
    val orderSent: Boolean = false,
    val orderDelivered: Boolean = false
)

data class MarketBuy(
    val marketLandId: Long,
    val code: String,
    val quantity: Int,
    val unitPrice: Int,
    val transportDelivery: DeliveryMethod = DeliveryMethod.MESSENGER,
    val transportSealed: Boolean = true
)

data class BardPlanItem(
    val actorId: Long,
    val deliveryMethod: DeliveryMethod = DeliveryMethod.MESSENGER,
    val sealedLetter: Boolean = true,
    val oral: Boolean = false,
    val feeSilver: Int,
    val notabilityLevel: Int,
    val generatedLocally: Boolean = false,
    val sendLater: Boolean = false,
    val invitationSent: Boolean = false
)

data class GuestPlanItem(
    val actorId: Long,
    val deliveryMethod: DeliveryMethod = DeliveryMethod.MESSENGER,
    val sealedLetter: Boolean = true,
    val oral: Boolean = false,
    val sendLater: Boolean = false,
    val invitationSent: Boolean = false
)

data class BallAlcoholPlan(
    val fromStocks: List<StockUse> = emptyList(),
    val fromMarket: List<MarketBuy> = emptyList()
) {
    fun wineSubtotal(): Int = fromMarket.sumOf { it.quantity * it.unitPrice }
    fun totalWineUnits(): Int = fromStocks.sumOf { it.quantity } + fromMarket.sumOf { it.quantity }
}

data class BallPlanDraft(
    val targetLandId: Long,
    val orderedByActorId: Long,
    val organizerActorId: Long,
    val eventTurn: Int?,
    val bardPlans: List<BardPlanItem>,
    val attendeePlans: List<GuestPlanItem>,
    val localMusicianCount: Int = 0,
    val inviteLocalPeople: Boolean = false,
    val venueStructureId: Long? = null,
    val localInviteMinimumTitleRank: Int = 0,
    val alcoholPlan: BallAlcoholPlan = BallAlcoholPlan(),
    val budgetFromStocks: List<StockUse> = emptyList(),
    val coinHolderOrderDeliveryMethod: DeliveryMethod = DeliveryMethod.MESSENGER,
    val coinHolderOrderSendLater: Boolean = false,
    val delegationOrderDeliveryMethod: DeliveryMethod = DeliveryMethod.MESSENGER,
    val delegationOrderSendLater: Boolean = false,
    val scale: String? = null,
    val useAlcohol: Boolean = false,
    val budgetUsesCoinHolder: Boolean = false,
    val coinHolderOrderSent: Boolean = false,
    val coinHolderOrderDelivered: Boolean = false,
    val delegationOrderSent: Boolean = false,
    val delegationOrderDelivered: Boolean = false
) {
    fun bardSubtotal(): Int = bardPlans.sumOf { it.feeSilver }
    fun wineSubtotal(): Int = alcoholPlan.wineSubtotal()
    fun totalBudget(): Int = bardSubtotal() + wineSubtotal()
    fun totalPerformers(): Int = bardPlans.size + localMusicianCount
    fun plannedBudgetTransferTotal(): Int = budgetFromStocks.sumOf { it.quantity }
}