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
    val userTicket: UserTicket? = null
)

class AppViewModel : ViewModel() {

    private val _state = MutableStateFlow(AppState(entities = SampleData.entities))
    val state: StateFlow<AppState> = _state.asStateFlow()

    private val _notificationEvents = MutableSharedFlow<NotificationEvent>()
    val notificationEvents: SharedFlow<NotificationEvent> = _notificationEvents.asSharedFlow()

    // ──────────────────────────────────────────────
    // Entity operations
    // ──────────────────────────────────────────────

    fun addEntity(name: String, latitude: Double, longitude: Double) {
        val newEntity = Entity(
            id = UUID.randomUUID().toString(),
            name = name,
            latitude = latitude,
            longitude = longitude
        )
        _state.update { it.copy(entities = it.entities + newEntity) }
    }

    // ──────────────────────────────────────────────
    // Desk operations
    // ──────────────────────────────────────────────

    fun addDesk(entityId: String, name: String, description: String) {
        val newDesk = Desk(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description
        )
        _state.update { state ->
            state.copy(
                entities = state.entities.map { entity ->
                    if (entity.id == entityId) {
                        entity.copy(desks = entity.desks + newDesk)
                    } else entity
                }
            )
        }
    }

    /** Manager presses "Next Ticket" — increments currentServing for the desk. */
    fun nextTicket(entityId: String, deskId: String) {
        var newServing = 0
        _state.update { state ->
            state.copy(
                entities = state.entities.map { entity ->
                    if (entity.id == entityId) {
                        entity.copy(
                            desks = entity.desks.map { desk ->
                                if (desk.id == deskId) {
                                    val updated = desk.copy(
                                        currentServing = desk.currentServing + 1
                                    )
                                    newServing = updated.currentServing
                                    updated
                                } else desk
                            }
                        )
                    } else entity
                }
            )
        }

        // Check notification trigger for the user holding a ticket at this desk
        val userTicket = _state.value.userTicket
        if (userTicket != null && userTicket.deskId == deskId) {
            val position = userTicket.ticketNumber - newServing
            if (position in 1..5) {
                viewModelScope.launch {
                    _notificationEvents.emit(
                        NotificationEvent(
                            title = "Bientôt votre tour !",
                            message = "Il reste $position personne(s) avant vous au guichet « ${userTicket.deskName} »."
                        )
                    )
                }
            }
        }
    }

    // ──────────────────────────────────────────────
    // Ticket operations
    // ──────────────────────────────────────────────

    /** Client takes a ticket at a desk. Returns the assigned ticket number. */
    fun takeTicket(entityId: String, deskId: String): Int {
        var issuedNumber = -1
        val entities = _state.value.entities
        val entity = entities.find { it.id == entityId } ?: return -1
        val desk = entity.desks.find { it.id == deskId } ?: return -1

        issuedNumber = desk.ticketCounter

        _state.update { state ->
            state.copy(
                entities = state.entities.map { e ->
                    if (e.id == entityId) {
                        e.copy(
                            desks = e.desks.map { d ->
                                if (d.id == deskId) {
                                    d.copy(ticketCounter = d.ticketCounter + 1)
                                } else d
                            }
                        )
                    } else e
                },
                userTicket = UserTicket(
                    ticketNumber = issuedNumber,
                    deskId = deskId,
                    entityId = entityId,
                    deskName = desk.name,
                    entityName = entity.name
                )
            )
        }
        return issuedNumber
    }

    fun clearUserTicket() {
        _state.update { it.copy(userTicket = null) }
    }

    // ──────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────

    fun getEntity(entityId: String): Entity? =
        _state.value.entities.find { it.id == entityId }

    fun getDesk(entityId: String, deskId: String): Desk? =
        getEntity(entityId)?.desks?.find { it.id == deskId }
}
