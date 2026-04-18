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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.queueapp.R
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
                title = {
                    Text(
                        if (entity != null) "${stringResource(R.string.add_desk_title)} — ${entity.name}"
                        else stringResource(R.string.add_desk_title)
                    )
                },
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
            Text(stringResource(R.string.desk_info), style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            OutlinedTextField(
                value          = name,
                onValueChange  = { name = it; nameError = false },
                label          = { Text(stringResource(R.string.desk_name_hint)) },
                placeholder    = { Text(stringResource(R.string.desk_name_placeholder)) },
                isError        = nameError,
                supportingText = if (nameError) ({ Text(stringResource(R.string.required_field)) }) else null,
                modifier       = Modifier.fillMaxWidth(),
                singleLine     = true
            )

            OutlinedTextField(
                value          = description,
                onValueChange  = { description = it; descError = false },
                label          = { Text(stringResource(R.string.desk_desc_hint)) },
                placeholder    = { Text(stringResource(R.string.desk_desc_placeholder)) },
                isError        = descError,
                supportingText = if (descError) ({ Text(stringResource(R.string.required_field)) }) else null,
                modifier       = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                maxLines       = 4
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick  = {
                    if (validate()) {
                        viewModel.addDesk(entityId, name.trim(), description.trim())
                        onSaved()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Save, null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.save_desk))
            }
        }
    }
}
