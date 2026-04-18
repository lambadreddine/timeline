package com.example.queueapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.queueapp.data.Desk
import com.example.queueapp.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntityDesksScreen(
    entityId: String,
    viewModel: AppViewModel,
    onAddDesk: () -> Unit,
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = onAddDesk,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Ajouter guichet",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
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
                    text       = "${entity.desks.size} guichet(s)",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(entity.desks) { desk ->
                ManagerDeskCard(
                    desk     = desk,
                    onNextTicket = { viewModel.nextTicket(entityId, desk.id) }
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
                            text  = "Aucun guichet. Appuyez sur + pour en ajouter.",
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
private fun ManagerDeskCard(
    desk: Desk,
    onNextTicket: () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                // Stats
                Column {
                    StatItem(label = "En cours de service", value = if (desk.currentServing == 0) "—" else "#${desk.currentServing}")
                    Spacer(modifier = Modifier.height(4.dp))
                    StatItem(label = "Prochain ticket", value = "#${desk.ticketCounter}")
                    Spacer(modifier = Modifier.height(4.dp))
                    StatItem(
                        label = "Tickets distribués",
                        value = "${(desk.ticketCounter - 1).coerceAtLeast(0)}"
                    )
                }

                // Next Ticket button
                FilledTonalButton(
                    onClick = onNextTicket
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ticket suivant")
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text  = "$label : ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text       = value,
            style      = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color      = MaterialTheme.colorScheme.primary
        )
    }
}
