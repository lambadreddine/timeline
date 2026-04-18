package com.example.queueapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.queueapp.data.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class AppState(
    val entities: List<Entity> = emptyList(),
    val userTickets: List<UserTicket> = emptyList()   // all active tickets the client holds
)

class AppViewModel : ViewModel() {

    private val _state = MutableStateFlow(AppState(entities = SampleData.entities))
    val state: StateFlow<AppState> = _state.asStateFlow()

    private val _notificationEvents = MutableSharedFlow<NotificationEvent>()
    val notificationEvents: SharedFlow<NotificationEvent> = _notificationEvents.asSharedFlow()

    // ──────────────────────────────────────────────
    // Entity
    // ──────────────────────────────────────────────

    fun addEntity(name: String, latitude: Double, longitude: Double) {
        _state.update { it.copy(entities = it.entities + Entity(name = name, latitude = latitude, longitude = longitude)) }
    }

    // ──────────────────────────────────────────────
    // Desk
    // ──────────────────────────────────────────────

    fun addDesk(entityId: String, name: String, description: String) {
        _state.update { state ->
            state.copy(entities = state.entities.map { entity ->
                if (entity.id == entityId) entity.copy(desks = entity.desks + Desk(name = name, description = description))
                else entity
            })
        }
    }

    /** Manager presses "Next Ticket". */
    fun nextTicket(entityId: String, deskId: String) {
        var newServing = 0
        var deskName = ""
        _state.update { state ->
            state.copy(entities = state.entities.map { entity ->
                if (entity.id != entityId) entity
                else entity.copy(desks = entity.desks.map { desk ->
                    if (desk.id != deskId) desk
                    else desk.copy(currentServing = desk.currentServing + 1).also {
                        newServing = it.currentServing
                        deskName  = it.name
                    }
                })
            })
        }

        // Notify any client whose ticket at this desk is within 5
        val matching = _state.value.userTickets.filter { it.deskId == deskId }
        matching.forEach { ticket ->
            val position = ticket.ticketNumber - newServing
            if (position in 1..5) {
                viewModelScope.launch {
                    _notificationEvents.emit(NotificationEvent(remainingCount = position, deskName = deskName))
                }
            }
        }
    }

    // ──────────────────────────────────────────────
    // Ticket
    // ──────────────────────────────────────────────

    /** Client takes a ticket. Returns the assigned ticket number, or -1 on error. */
    fun takeTicket(entityId: String, deskId: String): Int {
        val entity = _state.value.entities.find { it.id == entityId } ?: return -1
        val desk   = entity.desks.find { it.id == deskId }           ?: return -1

        // Prevent taking a second ticket at the same desk
        if (_state.value.userTickets.any { it.deskId == deskId }) return -1

        val issued = desk.ticketCounter
        _state.update { state ->
            state.copy(
                entities = state.entities.map { e ->
                    if (e.id != entityId) e
                    else e.copy(desks = e.desks.map { d ->
                        if (d.id != deskId) d else d.copy(ticketCounter = d.ticketCounter + 1)
                    })
                },
                userTickets = state.userTickets + UserTicket(
                    ticketNumber = issued,
                    deskId       = deskId,
                    entityId     = entityId,
                    deskName     = desk.name,
                    entityName   = entity.name
                )
            )
        }
        return issued
    }

    /** Cancel a single ticket by deskId. */
    fun cancelTicket(deskId: String) {
        _state.update { it.copy(userTickets = it.userTickets.filter { t -> t.deskId != deskId }) }
    }

    // ──────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────

    fun getEntity(entityId: String): Entity? = _state.value.entities.find { it.id == entityId }
    fun getDesk(entityId: String, deskId: String): Desk? = getEntity(entityId)?.desks?.find { it.id == deskId }
    fun getTicket(deskId: String): UserTicket? = _state.value.userTickets.find { it.deskId == deskId }
}
