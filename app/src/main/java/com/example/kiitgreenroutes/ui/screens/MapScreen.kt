package com.example.kiitgreenroutes.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kiitgreenroutes.data.BusSimulation
import com.example.kiitgreenroutes.data.model.Bus
import com.example.kiitgreenroutes.data.model.BusStatus
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    val kiitCampus = LatLng(20.3588, 85.8122)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(kiitCampus, 15f)
    }

    var buses by remember { mutableStateOf<List<Bus>>(emptyList()) }
    var selectedBus by remember { mutableStateOf<Bus?>(null) }
    
    LaunchedEffect(Unit) {
        BusSimulation.getBusUpdates().collectLatest {
            buses = it
            // Update selected bus info if it's still in the list
            if (selectedBus != null) {
                selectedBus = it.find { b -> b.id == selectedBus?.id }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Live Tracking", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text("Campus Shuttle Service", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White.copy(alpha = 0.9f))
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = permissionState.allPermissionsGranted,
                    mapType = MapType.NORMAL,
                    isTrafficEnabled = true
                ),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = false,
                    zoomControlsEnabled = false,
                    mapToolbarEnabled = false
                ),
                onMapClick = { selectedBus = null }
            ) {
                buses.forEach { bus ->
                    val markerState = rememberMarkerState(
                        key = bus.id,
                        position = LatLng(bus.latitude, bus.longitude)
                    )
                    markerState.position = LatLng(bus.latitude, bus.longitude)
                    
                    Marker(
                        state = markerState,
                        title = "Bus ${bus.busNumber}",
                        snippet = "Occupancy: ${bus.occupancy}%",
                        icon = BitmapDescriptorFactory.defaultMarker(
                            if (bus.status == BusStatus.DELAYED) BitmapDescriptorFactory.HUE_ORANGE 
                            else BitmapDescriptorFactory.HUE_GREEN
                        ),
                        onClick = {
                            selectedBus = bus
                            false
                        }
                    )
                }
                
                BusSimulation.routes.forEach { route ->
                    Polyline(
                        points = route.stops.map { LatLng(it.latitude, it.longitude) },
                        color = Color(0xFF4CAF50).copy(alpha = 0.3f),
                        width = 12f,
                        geodesic = true
                    )
                }
            }

            // Map Controls
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        if (permissionState.allPermissionsGranted) {
                            val locationResult = fusedLocationClient.lastLocation
                            locationResult.addOnSuccessListener { location: Location? ->
                                location?.let {
                                    val userLatLng = LatLng(it.latitude, it.longitude)
                                    scope.launch {
                                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(userLatLng, 16f))
                                    }
                                }
                            }
                        } else {
                            permissionState.launchMultiplePermissionRequest()
                        }
                    },
                    containerColor = Color.White,
                    contentColor = Color(0xFF1A237E),
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Rounded.MyLocation, contentDescription = "My Location")
                }
                
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(kiitCampus, 15f))
                        }
                    },
                    containerColor = Color.White,
                    contentColor = Color(0xFF4CAF50),
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Rounded.HomeWork, contentDescription = "Campus View")
                }
            }

            // Premium Bus Detail Card
            AnimatedVisibility(
                visible = selectedBus != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                selectedBus?.let { bus ->
                    BusDetailCard(bus = bus)
                }
            }
            
            if (buses.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    contentColor = Color.White
                ) {
                    Text(
                        "Searching for active shuttles...",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BusDetailCard(bus: Bus) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Rounded.DirectionsBus,
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp),
                            tint = Color(0xFF2E7D32)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Bus Route ${bus.busNumber}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF1A237E)
                        )
                        Text(
                            "Status: ${bus.status.name.replace("_", " ")}",
                            fontSize = 12.sp,
                            color = if (bus.status == BusStatus.DELAYED) Color.Red else Color.Gray
                        )
                    }
                }
                
                Surface(
                    color = Color(0xFF1A237E),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "${bus.etaMinutes} min",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(Modifier.height(20.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoChip(
                    icon = Icons.Rounded.Groups,
                    label = "Occupancy",
                    value = "${bus.occupancy}%",
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                InfoChip(
                    icon = Icons.Rounded.Update,
                    label = "Last Seen",
                    value = "Just now",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(Modifier.height(16.dp))
            
            Button(
                onClick = { /* Navigate to Bus */ },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Track this Shuttle", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun InfoChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color(0xFFF8F9FA),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
            Spacer(Modifier.width(8.dp))
            Column {
                Text(label, fontSize = 10.sp, color = Color.Gray)
                Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            }
        }
    }
}
