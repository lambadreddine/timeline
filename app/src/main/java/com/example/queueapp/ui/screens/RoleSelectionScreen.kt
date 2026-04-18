package com.example.queueapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RoleSelectionScreen(
    onClientSelected: () -> Unit,
    onManagerSelected: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App title
        Text(
            text       = "File d'Attente",
            style      = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text      = "Gestion des tickets et files d'attente",
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text  = "Sélectionnez votre profil",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Client button
        RoleCard(
            icon        = Icons.Filled.Person,
            title       = "Client",
            subtitle    = "Voir la carte et prendre un ticket",
            containerColor = MaterialTheme.colorScheme.primary,
            onClick     = onClientSelected
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Manager button
        RoleCard(
            icon        = Icons.Filled.AdminPanelSettings,
            title       = "Gestionnaire",
            subtitle    = "Gérer les entités, guichets et tickets",
            containerColor = MaterialTheme.colorScheme.secondary,
            onClick     = onManagerSelected
        )
    }
}

@Composable
private fun RoleCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    containerColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        onClick    = onClick,
        modifier   = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape      = RoundedCornerShape(16.dp),
        colors     = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier            = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier    = Modifier.size(48.dp),
                tint        = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text       = title,
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text  = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                )
            }
        }
    }
}
