package com.example.queueapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.queueapp.data.Entity
import com.example.queueapp.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerHomeScreen(
    viewModel: AppViewModel,
    onCreateEntity: () -> Unit,
    onEntityClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tableau de bord Gestionnaire") },
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
                onClick = onCreateEntity,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Ajouter entité", tint = MaterialTheme.colorScheme.onPrimary)
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
                    text       = "${state.entities.size} entité(s) enregistrée(s)",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(state.entities) { entity ->
                EntityManagerCard(
                    entity    = entity,
                    onClick   = { onEntityClick(entity.id) }
                )
            }

            if (state.entities.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text  = "Aucune entité. Appuyez sur + pour en créer une.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EntityManagerCard(
    entity: Entity,
    onClick: () -> Unit
) {
    Card(
        onClick   = onClick,
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint     = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text       = entity.name,
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text  = "${entity.desks.size} guichet(s)  |  Lat: %.4f".format(entity.latitude),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Voir guichets",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
