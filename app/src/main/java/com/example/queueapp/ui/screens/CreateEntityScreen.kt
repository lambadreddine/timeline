package com.example.queueapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.queueapp.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEntityScreen(
    viewModel: AppViewModel,
    onSaved: () -> Unit,
    onBackClick: () -> Unit
) {
    var name      by remember { mutableStateOf("") }
    var latStr    by remember { mutableStateOf("") }
    var lngStr    by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var latError  by remember { mutableStateOf(false) }
    var lngError  by remember { mutableStateOf(false) }

    fun validate(): Boolean {
        nameError = name.isBlank()
        latError  = latStr.toDoubleOrNull() == null
        lngError  = lngStr.toDoubleOrNull() == null
        return !nameError && !latError && !lngError
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nouvelle entité") },
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
                text  = "Informations de l'entité",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value           = name,
                onValueChange   = { name = it; nameError = false },
                label           = { Text("Nom de l'entité *") },
                placeholder     = { Text("ex. La Poste, Mairie…") },
                isError         = nameError,
                supportingText  = if (nameError) ({ Text("Le nom est requis") }) else null,
                modifier        = Modifier.fillMaxWidth(),
                singleLine      = true
            )

            Text(
                text  = "Coordonnées GPS",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value           = latStr,
                onValueChange   = { latStr = it; latError = false },
                label           = { Text("Latitude *") },
                placeholder     = { Text("ex. 36.3650") },
                isError         = latError,
                supportingText  = if (latError) ({ Text("Latitude invalide (ex. 36.3650)") }) else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier        = Modifier.fillMaxWidth(),
                singleLine      = true
            )

            OutlinedTextField(
                value           = lngStr,
                onValueChange   = { lngStr = it; lngError = false },
                label           = { Text("Longitude *") },
                placeholder     = { Text("ex. 6.6147") },
                isError         = lngError,
                supportingText  = if (lngError) ({ Text("Longitude invalide (ex. 6.6147)") }) else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier        = Modifier.fillMaxWidth(),
                singleLine      = true
            )

            // Helper hint
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    text     = "Constantine se situe environ à Lat: 36.3650, Lng: 6.6147.\n" +
                               "Utilisez Google Maps pour trouver les coordonnées exactes.",
                    modifier = Modifier.padding(12.dp),
                    style    = MaterialTheme.typography.bodySmall,
                    color    = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick  = {
                    if (validate()) {
                        viewModel.addEntity(
                            name      = name.trim(),
                            latitude  = latStr.toDouble(),
                            longitude = lngStr.toDouble()
                        )
                        onSaved()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enregistrer l'entité")
            }
        }
    }
}
