package com.example.queueapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.queueapp.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketInfoScreen(
    viewModel: AppViewModel,
    onBackClick: () -> Unit
) {
    val state     by viewModel.state.collectAsState()
    val userTicket = state.userTicket

    if (userTicket == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mon Ticket") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Aucun ticket actif.")
            }
        }
        return
    }

    // Get live desk state for reactive updates via StateFlow
    val desk = state.entities
        .find { it.id == userTicket.entityId }
        ?.desks?.find { it.id == userTicket.deskId }
    val currentServing = desk?.currentServing ?: 0
    val position = (userTicket.ticketNumber - currentServing).coerceAtLeast(0)
    val isCalled = currentServing >= userTicket.ticketNumber

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mon Ticket") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top)
        ) {
            // Entity & desk info
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text  = userTicket.entityName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text  = "Guichet : ${userTicket.deskName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Ticket number big display
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCalled) MaterialTheme.colorScheme.secondary
                        else MaterialTheme.colorScheme.primary
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text  = "Votre ticket",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                    )
                    Text(
                        text       = "#${userTicket.ticketNumber}",
                        fontSize   = 52.sp,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // Status cards row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatusCard(
                    modifier = Modifier.weight(1f),
                    label    = "En cours de service",
                    value    = if (currentServing == 0) "—" else "#$currentServing",
                    icon     = Icons.Filled.HourglassEmpty,
                    tint     = MaterialTheme.colorScheme.tertiary
                )
                StatusCard(
                    modifier = Modifier.weight(1f),
                    label    = "Votre position",
                    value    = if (isCalled) "Votre tour !" else "$position",
                    icon     = if (isCalled) Icons.Filled.CheckCircle else Icons.Filled.Notifications,
                    tint     = if (isCalled) MaterialTheme.colorScheme.secondary
                               else MaterialTheme.colorScheme.primary
                )
            }

            // Called banner
            if (isCalled) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint     = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text       = "C'est votre tour ! Présentez-vous au guichet « ${userTicket.deskName} ».",
                            style      = MaterialTheme.typography.bodyMedium,
                            color      = MaterialTheme.colorScheme.onSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else if (position in 1..5) {
                // Warning: approaching
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = null,
                            tint     = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text  = "Plus que $position personne(s) avant vous. Tenez-vous prêt !",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Cancel ticket button
            OutlinedButton(
                onClick  = {
                    viewModel.clearUserTicket()
                    onBackClick()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Annuler mon ticket")
            }
        }
    }
}

@Composable
private fun StatusCard(
    modifier: Modifier,
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier            = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = tint,
                modifier           = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text       = value,
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color      = tint,
                textAlign  = TextAlign.Center
            )
            Text(
                text      = label,
                style     = MaterialTheme.typography.labelSmall,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
