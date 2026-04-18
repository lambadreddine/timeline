package com.example.queueapp.data

import java.util.UUID

data class Entity(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val desks: List<Desk> = emptyList()
)

data class Desk(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val currentServing: Int = 0,   // ticket currently being served (0 = none)
    val ticketCounter: Int = 1      // next ticket number to issue
)

data class UserTicket(
    val ticketNumber: Int,
    val deskId: String,
    val entityId: String,
    val deskName: String,
    val entityName: String
)

/** Emitted by the ViewModel; formatted into a string in the UI layer. */
data class NotificationEvent(
    val remainingCount: Int,
    val deskName: String
)
