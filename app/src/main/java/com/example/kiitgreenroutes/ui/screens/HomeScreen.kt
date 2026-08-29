package com.example.kiitgreenroutes.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kiitgreenroutes.data.BusSimulation
import com.example.kiitgreenroutes.data.model.Bus
import com.example.kiitgreenroutes.data.model.BusStatus
import com.example.kiitgreenroutes.data.model.UserSession
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToMap: () -> Unit,
    onNavigateToRoutes: (String) -> Unit,
    onNavigateToSOS: () -> Unit,
    onNavigateToPass: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTimetable: () -> Unit
) {
    val backgroundColor = Color(0xFFF8F9FA)
    var buses by remember { mutableStateOf<List<Bus>>(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        BusSimulation.getBusUpdates().collectLatest {
            buses = it
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    HomeHeader(onProfileClick = onNavigateToSettings)
                }

                item {
                    // Professional Search bar implementation
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { 
                                searchQuery = it
                                isSearchActive = it.isNotEmpty()
                            },
                            placeholder = { Text("Search bus number (e.g. 12, 31, EIE)", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = Color(0xFF4CAF50)) },
                            trailingIcon = {
                                if (isSearchActive) {
                                    IconButton(onClick = { 
                                        searchQuery = ""
                                        isSearchActive = false
                                    }) {
                                        Icon(Icons.Rounded.Close, contentDescription = "Clear", tint = Color.Gray)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                    }
                }

                if (isSearchActive) {
                    val filteredRoutes = BusSimulation.routes.filter { 
                        it.number.equals(searchQuery, ignoreCase = true) || 
                        it.number.contains(searchQuery, ignoreCase = true) ||
                        it.name.contains(searchQuery, ignoreCase = true) 
                    }.distinctBy { it.number } // Show unique bus numbers
                    
                    if (filteredRoutes.isEmpty()) {
                        item {
                            Text(
                                "No buses found for \"$searchQuery\"",
                                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                        }
                    } else {
                        items(filteredRoutes) { route ->
                            ListItem(
                                headlineContent = { Text("Bus ${route.number}", fontWeight = FontWeight.Bold) },
                                supportingContent = { Text(route.name) },
                                leadingContent = { 
                                    Box(
                                        modifier = Modifier.size(40.dp).background(Color(0xFFE8F5E9), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Rounded.DirectionsBus, contentDescription = null, tint = Color(0xFF4CAF50))
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .clickable {
                                        onNavigateToRoutes(route.id)
                                        isSearchActive = false
                                        searchQuery = ""
                                    }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray)
                        }
                    }
                } else {
                    item {
                        CommunityAlertBanner()
                    }

                    item {
                        QuickActionsRow(
                            onMapClick = onNavigateToMap,
                            onRoutesClick = onNavigateToTimetable,
                            onPassClick = onNavigateToPass
                        )
                    }

                    if (buses.isNotEmpty()) {
                        item {
                            UltraNearestBusCard(
                                bus = buses.firstOrNull(),
                                onViewOnMap = onNavigateToMap,
                                onDetailsClick = onNavigateToRoutes
                            )
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Live Campus Buses",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF2D3436)
                                )
                                Text(
                                    text = "Real-Time",
                                    color = Color(0xFF4CAF50),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .background(Color(0xFFE8F5E9), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        items(buses) { bus ->
                            val route = BusSimulation.routes.find { it.id == bus.routeId }
                            UltraBusListItem(
                                bus = bus,
                                destination = route?.name?.split("→")?.lastOrNull()?.trim() ?: "Main Campus",
                                eta = "${(5..25).random()} min",
                                onNotifyClick = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Alert set for Bus ${bus.busNumber}!")
                                    }
                                },
                                onClick = { onNavigateToRoutes(bus.routeId) }
                            )
                        }
                    } else {
                        item {
                            NoServiceView()
                        }
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
fun NoServiceView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Rounded.BusAlert, 
            contentDescription = null, 
            modifier = Modifier.size(80.dp), 
            tint = Color.LightGray
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Bus Service Not Active",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
        Text(
            text = "Operational Hours: 07:00 AM - 07:00 PM\nPlease check back during service hours.",
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp).padding(horizontal = 20.dp)
        )
    }
}

@Composable
fun CommunityAlertBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFFF3E0),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Campaign, contentDescription = null, tint = Color(0xFFFF9800))
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Operational Hours: 7 AM - 7 PM Daily.",
                fontSize = 13.sp,
                color = Color(0xFFE65100),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun HomeHeader(onProfileClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome back,",
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${UserSession.userName ?: "Student"} 👋",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2D3436)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                IconButton(onClick = { /* TODO */ }) {
                    Icon(Icons.Rounded.NotificationsNone, contentDescription = "Notifications", tint = Color.Black)
                }
            }
            Surface(
                modifier = Modifier
                    .size(44.dp)
                    .clickable { onProfileClick() },
                shape = CircleShape,
                color = Color(0xFFE8F5E9),
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Person, contentDescription = "Profile", tint = Color(0xFF4CAF50))
                }
            }
        }
    }
}

@Composable
fun QuickActionsRow(
    onMapClick: () -> Unit,
    onRoutesClick: () -> Unit,
    onPassClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QuickActionItem(Icons.Rounded.Map, "Tracking", onMapClick)
        QuickActionItem(Icons.Rounded.Route, "Timetable", onRoutesClick)
        QuickActionItem(Icons.Rounded.MyLocation, "Nearby", {})
        QuickActionItem(Icons.Rounded.Badge, "My Pass", onPassClick)
    }
}

@Composable
fun QuickActionItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(28.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
    }
}

@Composable
fun UltraNearestBusCard(bus: Bus?, onViewOnMap: () -> Unit, onDetailsClick: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { bus?.routeId?.let { onDetailsClick(it) } },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Nearest Bus", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Text(
                    text = "See All", 
                    color = Color(0xFF4CAF50), 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.DirectionsBus, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    val route = BusSimulation.routes.find { it.id == bus?.routeId }
                    Text(text = "Bus ${bus?.busNumber ?: "N/A"}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = route?.name ?: "Ravi Talkies → Campus 25", color = Color.Gray, fontSize = 13.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "5 min", fontWeight = FontWeight.ExtraBold, color = Color(0xFF4CAF50), fontSize = 16.sp)
                    Text(text = "800m away", color = Color.Gray, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onViewOnMap,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Rounded.NearMe, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("View on Live Map", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun UltraBusListItem(bus: Bus, destination: String, eta: String, onNotifyClick: () -> Unit, onClick: () -> Unit) {
    val statusColor = when (bus.status) {
        BusStatus.AT_SOURCE -> Color(0xFF636E72)
        BusStatus.EN_ROUTE -> Color(0xFF4CAF50)
        BusStatus.DELAYED -> Color(0xFFF1C40F)
        BusStatus.REACHED -> Color(0xFF0984E3)
    }
    
    val statusText = when (bus.status) {
        BusStatus.AT_SOURCE -> "Inside Campus"
        BusStatus.EN_ROUTE -> "En Route"
        BusStatus.DELAYED -> "Stuck in Traffic"
        BusStatus.REACHED -> "Arrived"
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F3F4)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.DirectionsBus, contentDescription = null, tint = statusColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Bus ${bus.busNumber}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = statusText,
                            color = statusColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(text = destination, color = Color.Gray, fontSize = 13.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "${bus.etaMinutes} min", fontWeight = FontWeight.ExtraBold, color = Color(0xFF4CAF50))
                Text(text = "ETA", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}
