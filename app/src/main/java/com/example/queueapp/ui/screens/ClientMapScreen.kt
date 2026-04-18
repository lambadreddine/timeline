package com.example.queueapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.queueapp.R
import com.example.queueapp.data.Entity
import com.example.queueapp.viewmodel.AppViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

private val CONSTANTINE = GeoPoint(36.3650, 6.6147)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientMapScreen(
    viewModel: AppViewModel,
    onEntityClick: (String) -> Unit,
    onMyTickets: () -> Unit,
    onBackClick: () -> Unit
) {
    val state          by viewModel.state.collectAsState()
    val context        = LocalContext.current
    var selectedEntity by remember { mutableStateOf<Entity?>(null) }
    var mapViewRef     by remember { mutableStateOf<MapView?>(null) }
    val ticketCount    = state.userTickets.size

    val onMarkerClick by rememberUpdatedState { entity: Entity -> selectedEntity = entity }

    LaunchedEffect(Unit) {
        Configuration.getInstance().apply {
            load(context, context.getSharedPreferences("osmdroid", 0))
            userAgentValue = context.packageName
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.map_title)) },
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
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            // ── Map ──────────────────────────────────────────
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory  = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(14.0)
                        controller.setCenter(CONSTANTINE)
                        mapViewRef = this
                    }
                },
                update = { mapView ->
                    mapView.overlays.clear()
                    state.entities.forEach { entity ->
                        val marker = Marker(mapView).apply {
                            position = GeoPoint(entity.latitude, entity.longitude)
                            title    = entity.name
                            snippet  = context.getString(R.string.desks_available, entity.desks.size)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            setOnMarkerClickListener { _, _ -> onMarkerClick(entity); true }
                        }
                        mapView.overlays.add(marker)
                    }
                    mapView.invalidate()
                }
            )

            // ── Zoom controls (right side) ───────────────────
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SmallFloatingActionButton(
                    onClick           = { mapViewRef?.controller?.zoomIn() },
                    containerColor    = MaterialTheme.colorScheme.surface,
                    contentColor      = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Add, stringResource(R.string.zoom_in))
                }
                SmallFloatingActionButton(
                    onClick           = { mapViewRef?.controller?.zoomOut() },
                    containerColor    = MaterialTheme.colorScheme.surface,
                    contentColor      = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Remove, stringResource(R.string.zoom_out))
                }
            }

            // ── My Tickets FAB (bottom start) ────────────────
            BadgedBox(
                badge = {
                    if (ticketCount > 0) Badge { Text("$ticketCount") }
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                FloatingActionButton(
                    onClick        = onMyTickets,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor   = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(Icons.Filled.ConfirmationNumber, stringResource(R.string.my_tickets))
                }
            }

            // ── Hint (bottom center) when no marker selected ─
            if (selectedEntity == null) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(start = 80.dp, end = 16.dp, bottom = 16.dp)
                        .fillMaxWidth(),
                    color          = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    shape          = MaterialTheme.shapes.medium,
                    tonalElevation = 4.dp
                ) {
                    Text(
                        text     = stringResource(R.string.map_hint),
                        modifier = Modifier.padding(12.dp),
                        style    = MaterialTheme.typography.bodyMedium,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Bottom sheet on marker tap
    selectedEntity?.let { entity ->
        EntityMarkerSheet(
            entity       = entity,
            onDismiss    = { selectedEntity = null },
            onViewDetail = { selectedEntity = null; onEntityClick(entity.id) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntityMarkerSheet(entity: Entity, onDismiss: () -> Unit, onViewDetail: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)) {
            Text(entity.name, style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.desks_available, entity.desks.size),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))

            entity.desks.forEach { desk ->
                Row(
                    modifier          = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(desk.name, style = MaterialTheme.typography.titleMedium)
                        Text(desk.description, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(
                        stringResource(R.string.serving_now,
                            if (desk.currentServing == 0) stringResource(R.string.none_serving)
                            else "#${desk.currentServing}"),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            }

            Spacer(Modifier.height(16.dp))
            Button(onClick = onViewDetail, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.take_ticket))
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
