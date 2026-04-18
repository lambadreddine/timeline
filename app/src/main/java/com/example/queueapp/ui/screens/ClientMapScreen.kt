package com.example.queueapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.queueapp.data.Entity
import com.example.queueapp.viewmodel.AppViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

// Constantine, Algeria
private val CONSTANTINE = GeoPoint(36.3650, 6.6147)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientMapScreen(
    viewModel: AppViewModel,
    onEntityClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val state   by viewModel.state.collectAsState()
    val context = LocalContext.current

    var selectedEntity by remember { mutableStateOf<Entity?>(null) }

    // Keep a stable callback reference so the AndroidView update block
    // always uses the latest selectedEntity setter without recreating the view.
    val onMarkerClick by rememberUpdatedState { entity: Entity ->
        selectedEntity = entity
    }

    // Initialise OSMDroid configuration once
    LaunchedEffect(Unit) {
        Configuration.getInstance().apply {
            load(context, context.getSharedPreferences("osmdroid", 0))
            userAgentValue = context.packageName
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carte des Services") },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory  = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(14.0)
                        controller.setCenter(CONSTANTINE)
                    }
                },
                update = { mapView ->
                    // Rebuild markers on every recomposition so new entities appear
                    mapView.overlays.clear()
                    state.entities.forEach { entity ->
                        val marker = Marker(mapView).apply {
                            position  = GeoPoint(entity.latitude, entity.longitude)
                            title     = entity.name
                            snippet   = "${entity.desks.size} guichet(s)"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            setOnMarkerClickListener { _, _ ->
                                onMarkerClick(entity)
                                true
                            }
                        }
                        mapView.overlays.add(marker)
                    }
                    mapView.invalidate()
                }
            )

            // Hint when no marker is selected
            if (selectedEntity == null) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    color          = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    shape          = MaterialTheme.shapes.medium,
                    tonalElevation = 4.dp
                ) {
                    Text(
                        text     = "Appuyez sur un marqueur pour voir les détails",
                        modifier = Modifier.padding(16.dp),
                        style    = MaterialTheme.typography.bodyMedium,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Bottom sheet when a marker is tapped
    selectedEntity?.let { entity ->
        EntityMarkerSheet(
            entity       = entity,
            onDismiss    = { selectedEntity = null },
            onViewDetail = {
                selectedEntity = null
                onEntityClick(entity.id)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntityMarkerSheet(
    entity: Entity,
    onDismiss: () -> Unit,
    onViewDetail: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text       = entity.name,
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text  = "${entity.desks.size} guichet(s) disponible(s)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text  = "Lat: %.4f  |  Lng: %.4f".format(entity.latitude, entity.longitude),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            entity.desks.forEach { desk ->
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = desk.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text  = desk.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text  = "En cours: ${if (desk.currentServing == 0) "—" else "#${desk.currentServing}"}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick  = onViewDetail,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Prendre un ticket")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
