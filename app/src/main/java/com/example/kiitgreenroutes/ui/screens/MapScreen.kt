package com.example.kiitgreenroutes.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.kiitgreenroutes.data.model.Stop
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.*

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    initialBusId: String? = null,
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
    var showNearbySheet by remember { mutableStateOf(false) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var userSpeed by remember { mutableStateOf(0f) }
    var isTrackingSpecificBus by remember { mutableStateOf(initialBusId != null) }
    var hasCenteredOnLiveBuses by remember { mutableStateOf(false) }
    var isMapLoaded by remember { mutableStateOf(false) }
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    
    var searchQuery by remember { mutableStateOf("") }

    val filteredStops = remember(searchQuery) {
        if (searchQuery.isBlank()) emptyList()
        else BusSimulation.stops.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    LaunchedEffect(Unit) {
        BusSimulation.getBusUpdates().collectLatest { updatedBuses ->
            buses = updatedBuses

            if (updatedBuses.isNotEmpty() && !isTrackingSpecificBus && selectedBus == null) {
                hasCenteredOnLiveBuses = false
            }
            
            if (isTrackingSpecificBus && initialBusId != null) {
                val foundBus = updatedBuses.find { it.busNumber == initialBusId || it.id == initialBusId }
                if (foundBus != null) {
                    selectedBus = foundBus
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(foundBus.latitude, foundBus.longitude), 16f))
                    isTrackingSpecificBus = false
                }
            }

            if (selectedBus != null) {
                selectedBus = updatedBuses.find { b -> b.id == selectedBus?.id }
            }
        }
    }

    LaunchedEffect(isMapLoaded, buses, selectedBus, isTrackingSpecificBus) {
        if (!isMapLoaded || hasCenteredOnLiveBuses || selectedBus != null || isTrackingSpecificBus || buses.isEmpty()) return@LaunchedEffect

        val boundsBuilder = LatLngBounds.builder()
        buses.forEach { bus -> boundsBuilder.include(LatLng(bus.latitude, bus.longitude)) }
        BusSimulation.stops.take(3).forEach { stop -> boundsBuilder.include(LatLng(stop.latitude, stop.longitude)) }

        scope.launch {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 120)
            )
        }
        hasCenteredOnLiveBuses = true
    }

    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted) {
            val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 2000
            ).build()

            val locationCallback = object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                    result.lastLocation?.let {
                        userLocation = LatLng(it.latitude, it.longitude)
                        userSpeed = it.speed * 3.6f // Convert m/s to km/h
                    }
                }
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, android.os.Looper.getMainLooper())
        }
    }

    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(
                color = Color.White,
                tonalElevation = 8.dp,
                shadowElevation = 4.dp // Added shadow for better visibility
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding() // Ensures it doesn't hide behind notch
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                        }
                        
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search Campus Stops...") },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .padding(horizontal = 4.dp),
                            shape = RoundedCornerShape(26.dp),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF1F3F4),
                                unfocusedContainerColor = Color(0xFFF1F3F4),
                                disabledContainerColor = Color(0xFFF1F3F4),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = Color(0xFF1A237E)) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Rounded.Close, contentDescription = "Clear")
                                    }
                                }
                            }
                        )

                        IconButton(onClick = { showNearbySheet = true }) {
                            Icon(Icons.Rounded.NearMe, contentDescription = "Nearby", tint = Color(0xFF4CAF50))
                        }
                    }

                    AnimatedVisibility(visible = searchQuery.isNotEmpty() && filteredStops.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column {
                                filteredStops.take(3).forEach { stop ->
                                    ListItem(
                                        headlineContent = { Text(stop.name) },
                                        leadingContent = { Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = Color.Gray) },
                                        modifier = Modifier.clickable {
                                            searchQuery = ""
                                            scope.launch {
                                                cameraPositionState.animate(
                                                    CameraUpdateFactory.newLatLngZoom(LatLng(stop.latitude, stop.longitude), 17f)
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapLoaded = { isMapLoaded = true },
                properties = MapProperties(
                    isMyLocationEnabled = permissionState.allPermissionsGranted,
                    mapType = mapType,
                    isTrafficEnabled = true
                ),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = false,
                    zoomControlsEnabled = false,
                    mapToolbarEnabled = false
                ),
                onMapClick = { selectedBus = null }
            ) {
                userLocation?.let {
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val radius by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 100f,
                        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Restart),
                        label = "radius"
                    )
                    Circle(
                        center = it,
                        radius = radius.toDouble(),
                        fillColor = Color(0xFF2196F3).copy(alpha = (1 - radius/100) * 0.3f),
                        strokeColor = Color.Transparent
                    )
                }

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

                BusSimulation.stops.forEach { stop ->
                    Marker(
                        state = rememberMarkerState(position = LatLng(stop.latitude, stop.longitude)),
                        title = stop.name,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                        alpha = 0.7f
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

            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        mapType = if (mapType == MapType.NORMAL) MapType.SATELLITE else MapType.NORMAL
                    },
                    containerColor = Color.White,
                    contentColor = Color(0xFF673AB7),
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        if (mapType == MapType.NORMAL) Icons.Rounded.Layers else Icons.Rounded.Map,
                        contentDescription = "Map Style"
                    )
                }

                FloatingActionButton(
                    onClick = {
                        if (permissionState.allPermissionsGranted) {
                            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                                location?.let {
                                    val userLatLng = LatLng(it.latitude, it.longitude)
                                    userLocation = userLatLng
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
            
            if (userSpeed > 1) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    contentColor = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Speed, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF00E676))
                        Spacer(Modifier.width(8.dp))
                        Text("${userSpeed.toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text(" km/h", fontSize = 10.sp, color = Color.LightGray)
                    }
                }
            }
            
            if (buses.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            color = Color(0xFFFFF3E0),
                            shape = CircleShape,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(
                                Icons.Rounded.BusAlert,
                                contentDescription = null,
                                modifier = Modifier.padding(16.dp),
                                tint = Color(0xFFFF9800)
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No Shuttles Active",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF1A237E)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Current service is offline. Buses usually operate between 7:00 AM and 9:00 PM.",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else if (initialBusId != null && selectedBus == null) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Red.copy(alpha = 0.8f),
                    contentColor = Color.White
                ) {
                    Text(
                        "Bus $initialBusId not found on route",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showNearbySheet) {
        ModalBottomSheet(
            onDismissRequest = { showNearbySheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            NearbyStopsContent(
                userLocation = userLocation,
                stops = BusSimulation.stops,
                onStopClick = { stop ->
                    scope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(LatLng(stop.latitude, stop.longitude), 17f)
                        )
                        showNearbySheet = false
                    }
                }
            )
        }
    }
}

@Composable
fun NearbyStopsContent(
    userLocation: LatLng?,
    stops: List<Stop>,
    onStopClick: (Stop) -> Unit
) {
    val nearbyStops: List<Pair<Stop, Double>> = remember(userLocation) {
        if (userLocation == null) {
            stops.take(5).map { it to 0.0 }
        } else {
            stops.map { stop ->
                val distance = calculateDistance(
                    userLocation.latitude, userLocation.longitude,
                    stop.latitude, stop.longitude
                )
                stop to distance
            }.sortedBy { it.second }.take(6)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            "Nearby Shuttle Stops",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1A237E)
        )
        Text(
            "Found ${nearbyStops.size} stops near your current location",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(nearbyStops) { (stop, distance) ->
                val distText = if (userLocation != null) {
                    if (distance < 1) "${(distance * 1000).toInt()}m away"
                    else "%.1f km away".format(distance)
                } else "Campus Area"

                Surface(
                    onClick = { onStopClick(stop) },
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF8F9FA),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0xFFE8EAF6),
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Rounded.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.padding(8.dp),
                                tint = Color(0xFF1A237E)
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stop.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(distText, fontSize = 12.sp, color = Color(0xFF4CAF50))
                        }
                        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = Color.LightGray)
                    }
                }
            }
        }
    }
}

fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371
    val latDistance = Math.toRadians(lat2 - lat1)
    val lonDistance = Math.toRadians(lon2 - lon1)
    val a = sin(latDistance / 2) * sin(latDistance / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(lonDistance / 2) * sin(lonDistance / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
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
