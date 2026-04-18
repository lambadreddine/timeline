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
    val currentServing: Int = 0,   // ticket number currently being served (0 = none yet)
    val ticketCounter: Int = 1      // next ticket number to be issued
)

data class UserTicket(
    val ticketNumber: Int,
    val deskId: String,
    val entityId: String,
    val deskName: String,
    val entityName: String
)

data class NotificationEvent(
    val title: String,
    val message: String
)
