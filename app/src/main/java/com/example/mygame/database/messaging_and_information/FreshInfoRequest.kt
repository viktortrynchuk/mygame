package com.example.mygame.database.messaging_and_information

data class FreshInfoRequest(
    val messageType: MessageType,
    val deliveryMethod: DeliveryMethod
)