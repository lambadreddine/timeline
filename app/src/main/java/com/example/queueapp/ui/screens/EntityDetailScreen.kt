package com.example.queueapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.queueapp.data.Desk
import com.example.queueapp.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntityDetailScreen(
    entityId: String,
    viewModel: AppViewModel,
    onTicketTaken: () -> Unit,
    onBackClick: () -> Unit
) {
    val state  by viewModel.state.collectAsState()
    val entity = state.entities.find { it.id == entityId }

    if (entity == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Entité introuvable.")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(entity.name) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding      = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text(
                    text       = "Sélectionnez un guichet",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(entity.desks) { desk ->
                DeskTicketCard(
                    desk        = desk,
                    hasTicket   = state.userTicket?.deskId == desk.id,
                    onTakeTicket = {
                        viewModel.takeTicket(entity.id, desk.id)
                        onTicketTaken()
                    }
                )
            }

            if (entity.desks.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = "Aucun guichet disponible pour cette entité.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeskTicketCard(
    desk: Desk,
    hasTicket: Boolean,
    onTakeTicket: () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = desk.name,
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text  = desk.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text  = "En cours",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text       = if (desk.currentServing == 0) "—" else "#${desk.currentServing}",
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = "Prochain ticket : #${desk.ticketCounter}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick  = onTakeTicket,
                    enabled  = !hasTicket,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ConfirmationNumber,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (hasTicket) "Ticket pris" else "Prendre un ticket")
                }
            }
        }
    }
}
