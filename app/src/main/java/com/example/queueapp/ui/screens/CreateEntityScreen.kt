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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.queueapp.R
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
                title = { Text(stringResource(R.string.create_entity_title)) },
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
            modifier            = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.entity_info), style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            OutlinedTextField(
                value          = name,
                onValueChange  = { name = it; nameError = false },
                label          = { Text(stringResource(R.string.entity_name_hint)) },
                placeholder    = { Text(stringResource(R.string.entity_name_placeholder)) },
                isError        = nameError,
                supportingText = if (nameError) ({ Text(stringResource(R.string.required_field)) }) else null,
                modifier       = Modifier.fillMaxWidth(),
                singleLine     = true
            )

            Text(stringResource(R.string.gps_coords), style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            OutlinedTextField(
                value           = latStr,
                onValueChange   = { latStr = it; latError = false },
                label           = { Text(stringResource(R.string.latitude_hint)) },
                isError         = latError,
                supportingText  = if (latError) ({ Text(stringResource(R.string.invalid_lat)) }) else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier        = Modifier.fillMaxWidth(),
                singleLine      = true
            )

            OutlinedTextField(
                value           = lngStr,
                onValueChange   = { lngStr = it; lngError = false },
                label           = { Text(stringResource(R.string.longitude_hint)) },
                isError         = lngError,
                supportingText  = if (lngError) ({ Text(stringResource(R.string.invalid_lng)) }) else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier        = Modifier.fillMaxWidth(),
                singleLine      = true
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(stringResource(R.string.constantine_hint), modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer)
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick  = {
                    if (validate()) {
                        viewModel.addEntity(name.trim(), latStr.toDouble(), lngStr.toDouble())
                        onSaved()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Save, null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.save_entity))
            }
        }
    }
}
