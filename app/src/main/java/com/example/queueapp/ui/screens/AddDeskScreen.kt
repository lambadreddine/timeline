package com.example.queueapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.queueapp.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeskScreen(
    entityId: String,
    viewModel: AppViewModel,
    onSaved: () -> Unit,
    onBackClick: () -> Unit
) {
    val state  by viewModel.state.collectAsState()
    val entity = state.entities.find { it.id == entityId }

    var name        by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var nameError   by remember { mutableStateOf(false) }
    var descError   by remember { mutableStateOf(false) }

    fun validate(): Boolean {
        nameError = name.isBlank()
        descError = description.isBlank()
        return !nameError && !descError
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nouveau guichet${if (entity != null) " — ${entity.name}" else ""}") },
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
            modifier            = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text  = "Informations du guichet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value         = name,
                onValueChange = { name = it; nameError = false },
                label         = { Text("Nom du guichet *") },
                placeholder   = { Text("ex. Courrier, État Civil…") },
                isError       = nameError,
                supportingText = if (nameError) ({ Text("Le nom est requis") }) else null,
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true
            )

            OutlinedTextField(
                value         = description,
                onValueChange = { description = it; descError = false },
                label         = { Text("Description *") },
                placeholder   = { Text("ex. Réception et envoi de colis") },
                isError       = descError,
                supportingText = if (descError) ({ Text("La description est requise") }) else null,
                modifier      = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                maxLines      = 4
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick  = {
                    if (validate()) {
                        viewModel.addDesk(
                            entityId    = entityId,
                            name        = name.trim(),
                            description = description.trim()
                        )
                        onSaved()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enregistrer le guichet")
            }
        }
    }
}
