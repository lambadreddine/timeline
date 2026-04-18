package com.example.queueapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.queueapp.R
import com.example.queueapp.data.UserTicket
import com.example.queueapp.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTicketsScreen(
    viewModel: AppViewModel,
    onTicketClick: (deskId: String) -> Unit,
    onBackClick: () -> Unit
) {
    val state   by viewModel.state.collectAsState()
    val tickets = state.userTickets

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_tickets_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor        = MaterialTheme.colorScheme.primary,
                    titleContentColor     = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        if (tickets.isEmpty()) {
            Box(
                modifier         = Modifier.fillMaxSize().padding(innerPadding).padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.ConfirmationNumber,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint     = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.no_tickets),
                        style     = MaterialTheme.typography.bodyLarge,
                        color     = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier            = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding      = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    Text(
                        stringResource(R.string.active_tickets_count, tickets.size),
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color      = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(tickets, key = { it.deskId }) { ticket ->
                    val desk           = state.entities.find { it.id == ticket.entityId }?.desks?.find { it.id == ticket.deskId }
                    val currentServing = desk?.currentServing ?: 0
                    val position       = (ticket.ticketNumber - currentServing).coerceAtLeast(0)
                    val isCalled       = currentServing >= ticket.ticketNumber

                    TicketSummaryCard(
                        ticket         = ticket,
                        currentServing = currentServing,
                        position       = position,
                        isCalled       = isCalled,
                        onClick        = { onTicketClick(ticket.deskId) },
                        onCancel       = { viewModel.cancelTicket(ticket.deskId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TicketSummaryCard(
    ticket: UserTicket,
    currentServing: Int,
    position: Int,
    isCalled: Boolean,
    onClick: () -> Unit,
    onCancel: () -> Unit
) {
    val containerColor = when {
        isCalled       -> MaterialTheme.colorScheme.secondaryContainer
        position in 1..5 -> MaterialTheme.colorScheme.tertiaryContainer
        else           -> MaterialTheme.colorScheme.surface
    }

    Card(
        onClick   = onClick,
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors    = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: ticket number circle
            Box(
                modifier         = Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCalled) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        "#${ticket.ticketNumber}",
                        modifier   = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // Middle: entity + desk info
            Column(modifier = Modifier.weight(1f)) {
                Text(ticket.entityName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(ticket.deskName, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        if (isCalled) Icons.Filled.CheckCircle else Icons.Filled.HourglassEmpty,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint     = if (isCalled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        when {
                            isCalled           -> stringResource(R.string.your_turn)
                            position in 1..5   -> stringResource(R.string.almost_your_turn, position)
                            else               -> stringResource(R.string.waiting_count, position)
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = when {
                            isCalled         -> MaterialTheme.colorScheme.secondary
                            position in 1..5 -> MaterialTheme.colorScheme.tertiary
                            else             -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            // Right: cancel icon
            IconButton(onClick = onCancel) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = stringResource(R.string.cancel_ticket),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}
