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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.queueapp.R

@Composable
fun RoleSelectionScreen(
    onClientSelected: () -> Unit,
    onManagerSelected: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement   = Arrangement.Center,
        horizontalAlignment   = Alignment.CenterHorizontally
    ) {
        Text(
            text       = stringResource(R.string.app_name),
            style      = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text      = stringResource(R.string.app_subtitle),
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(64.dp))

        Text(
            text  = stringResource(R.string.select_profile),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(32.dp))

        RoleCard(
            icon           = Icons.Filled.Person,
            title          = stringResource(R.string.role_client),
            subtitle       = stringResource(R.string.role_client_desc),
            containerColor = MaterialTheme.colorScheme.primary,
            onClick        = onClientSelected
        )
        Spacer(Modifier.height(20.dp))
        RoleCard(
            icon           = Icons.Filled.AdminPanelSettings,
            title          = stringResource(R.string.role_manager),
            subtitle       = stringResource(R.string.role_manager_desc),
            containerColor = MaterialTheme.colorScheme.secondary,
            onClick        = onManagerSelected
        )
    }
}

@Composable
private fun RoleCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    containerColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick  = onClick,
        modifier = Modifier.fillMaxWidth().height(120.dp),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier              = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(icon, contentDescription = title, modifier = Modifier.size(48.dp), tint = Color.White)
            Spacer(Modifier.width(20.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
            }
        }
    }
}
