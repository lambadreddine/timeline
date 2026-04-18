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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.queueapp.R
import com.example.queueapp.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketInfoScreen(
    deskId: String,
    viewModel: AppViewModel,
    onBackClick: () -> Unit
) {
    val state      by viewModel.state.collectAsState()
    val userTicket = state.userTickets.find { it.deskId == deskId }

    if (userTicket == null) {
        Scaffold(topBar = {
            TopAppBar(title = { Text(stringResource(R.string.your_ticket_title)) },
                navigationIcon = { IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back)) } })
        }) { p ->
            Box(Modifier.fillMaxSize().padding(p), Alignment.Center) {
                Text(stringResource(R.string.no_active_ticket))
            }
        }
        return
    }

    val desk           = state.entities.find { it.id == userTicket.entityId }?.desks?.find { it.id == deskId }
    val currentServing = desk?.currentServing ?: 0
    val position       = (userTicket.ticketNumber - currentServing).coerceAtLeast(0)
    val isCalled       = currentServing >= userTicket.ticketNumber

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.your_ticket_title)) },
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
        Column(
            modifier                = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
            horizontalAlignment     = Alignment.CenterHorizontally,
            verticalArrangement     = Arrangement.spacedBy(20.dp, Alignment.Top)
        ) {
            // Entity + desk card
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(userTicket.entityName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(stringResource(R.string.desk_label, userTicket.deskName),
                        style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Big ticket circle
            Box(
                modifier         = Modifier.size(180.dp).clip(CircleShape)
                    .background(if (isCalled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.your_ticket_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    Text(
                        stringResource(R.string.ticket_number, userTicket.ticketNumber),
                        fontSize   = 52.sp,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // Stats row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    label    = stringResource(R.string.serving_label),
                    value    = if (currentServing == 0) stringResource(R.string.none_serving) else "#$currentServing",
                    icon     = Icons.Filled.HourglassEmpty,
                    tint     = MaterialTheme.colorScheme.tertiary
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    label    = stringResource(R.string.position_label),
                    value    = if (isCalled) stringResource(R.string.your_turn) else "$position",
                    icon     = if (isCalled) Icons.Filled.CheckCircle else Icons.Filled.Notifications,
                    tint     = if (isCalled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                )
            }

            // Status banners
            when {
                isCalled -> StatusBanner(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    icon           = Icons.Filled.CheckCircle,
                    message        = stringResource(R.string.present_yourself, userTicket.deskName),
                    onContent      = MaterialTheme.colorScheme.onSecondary
                )
                position in 1..5 -> StatusBanner(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    icon           = Icons.Filled.Notifications,
                    message        = stringResource(R.string.almost_your_turn, position),
                    onContent      = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }

            Spacer(Modifier.weight(1f))

            OutlinedButton(
                onClick  = { viewModel.cancelTicket(deskId); onBackClick() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.cancel_ticket))
            }
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, label: String, value: String, icon: ImageVector, tint: androidx.compose.ui.graphics.Color) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = tint, textAlign = TextAlign.Center)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun StatusBanner(containerColor: androidx.compose.ui.graphics.Color, icon: ImageVector, message: String, onContent: androidx.compose.ui.graphics.Color) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = onContent, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Text(message, style = MaterialTheme.typography.bodyMedium, color = onContent, fontWeight = FontWeight.SemiBold)
        }
    }
}
