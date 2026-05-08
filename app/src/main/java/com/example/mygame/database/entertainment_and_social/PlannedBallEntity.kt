package com.example.mygame.database.entertainment_and_social

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mygame.database.messaging_and_information.DeliveryMethod
import com.example.mygame.database.messaging_and_information.MessageType

@Entity(tableName = "planned_ball")
data class PlannedBallEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderedByActorId: Long,
    val organizerActorId: Long,
    val landId: Long,
    val eventTurn: Int,
    val venueStructureId: Long?,
    val status: String,
    val inviteLocalPeople: Boolean,
    val localInviteMinimumTitleRank: Int = 0,
    val localMusicianCount: Int = 0,
    val scale: String? = null,
    val useAlcohol: Boolean = false,
    val budgetUsesCoinHolder: Boolean = false,
    val coinHolderOrderDeliveryMethod: DeliveryMethod = DeliveryMethod.MESSENGER,
    val coinHolderOrderSendLater: Boolean = false,
    val delegationOrderDeliveryMethod: DeliveryMethod = DeliveryMethod.MESSENGER,
    val delegationOrderSendLater: Boolean = false,
    val coinHolderOrderSent: Boolean = false,
    val coinHolderOrderDelivered: Boolean = false,
    val delegationOrderSent: Boolean = false,
    val delegationOrderDelivered: Boolean = false,
    val finalizedBallEventId: Long? = null
)

@Entity(tableName = "planned_ball_bard")
data class PlannedBallBardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plannedBallId: Long,
    val bardActorId: Long,
    val invitationSent: Boolean,
    val accepted: Boolean? = null,
    val deliveryMethod: DeliveryMethod = DeliveryMethod.MESSENGER,
    val messageType: MessageType = MessageType.SEALED_LETTER,
    val feeSilver: Int = 0,
    val notabilityLevel: Int = 1,
    val generatedLocally: Boolean = false,
    val sendLater: Boolean = false
)

@Entity(tableName = "planned_ball_guest")
data class PlannedBallGuestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plannedBallId: Long,
    val actorId: Long,
    val invitationSent: Boolean,
    val accepted: Boolean? = null,
    val deliveryMethod: DeliveryMethod = DeliveryMethod.MESSENGER,
    val messageType: MessageType = MessageType.SEALED_LETTER,
    val sendLater: Boolean = false
)

@Entity(tableName = "planned_ball_alcohol")
data class PlannedBallAlcoholEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plannedBallId: Long,
    val code: String,
    val quantity: Int,
    val sourceLandId: Long?,
    val sourceType: String,
    val unitPrice: Int = 0,
    val purchased: Boolean = false,
    val transported: Boolean = false,
    val transportDeliveryMethod: DeliveryMethod = DeliveryMethod.MESSENGER,
    val transportSealedLetter: Boolean = true,
    val transportSendLater: Boolean = false,
    val transportOrderSent: Boolean = false,
    val transportOrderDelivered: Boolean = false
)