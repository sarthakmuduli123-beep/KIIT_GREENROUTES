package com.example.kiitgreenroutes.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kiitgreenroutes.data.BusSimulation
import com.example.kiitgreenroutes.data.model.Bus
import com.example.kiitgreenroutes.data.model.BusStatus
import com.example.kiitgreenroutes.data.model.Route
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailsScreen(routeId: String, onBack: () -> Unit) {
    val route = remember { BusSimulation.routes.find { it.id == routeId } }
    var buses by remember { mutableStateOf<List<Bus>>(emptyList()) }
    val activeBus = buses.find { it.routeId == routeId }

    LaunchedEffect(Unit) {
        BusSimulation.getBusUpdates().collectLatest {
            buses = it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Bus ${route?.number ?: ""}", fontWeight = FontWeight.ExtraBold)
                        Text(route?.name ?: "", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (route == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Route not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF8F9FA))
            ) {
                // Status Header
                BusStatusCard(activeBus)

                // Metro Style Progress Bar
                Text(
                    "Route Progress",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 20.dp)
                ) {
                    itemsIndexed(route.stops) { index, stop ->
                        val isPassed = activeBus?.let { it.currentStopIndex > index } ?: false
                        val isCurrent = activeBus?.let { it.currentStopIndex == index } ?: false
                        
                        StopItem(
                            stopName = stop.name,
                            isPassed = isPassed,
                            isCurrent = isCurrent,
                            isLast = index == route.stops.size - 1,
                            busStatus = activeBus?.status ?: BusStatus.AT_SOURCE
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BusStatusCard(bus: Bus?) {
    val statusColor = when (bus?.status) {
        BusStatus.AT_SOURCE -> Color(0xFF636E72)
        BusStatus.EN_ROUTE -> Color(0xFF4CAF50)
        BusStatus.DELAYED -> Color(0xFFF1C40F)
        BusStatus.REACHED -> Color(0xFF0984E3)
        null -> Color.LightGray
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(statusColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (bus?.status == BusStatus.DELAYED) Icons.Rounded.Traffic else Icons.Rounded.DirectionsBus,
                    contentDescription = null,
                    tint = statusColor
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = when(bus?.status) {
                        BusStatus.AT_SOURCE -> "Departing from Campus"
                        BusStatus.EN_ROUTE -> "Bus is En Route"
                        BusStatus.DELAYED -> "Delayed by Traffic"
                        BusStatus.REACHED -> "Reached Destination"
                        else -> "Service Starting Soon"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = if (bus != null) "Next Stop ETA: ${bus.etaMinutes} mins" else "Waiting for update...",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun StopItem(
    stopName: String,
    isPassed: Boolean,
    isCurrent: Boolean,
    isLast: Boolean,
    busStatus: BusStatus
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Timeline Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCurrent -> Color(0xFF4CAF50)
                            isPassed -> Color.LightGray
                            else -> Color.White
                        }
                    )
                    .then(
                        if (!isPassed && !isCurrent) Modifier.background(Color.White, CircleShape).padding(2.dp).background(Color.LightGray, CircleShape)
                        else Modifier
                    )
            )
            
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(if (isPassed) Color.LightGray else Color(0xFFE0E0E0))
                )
            }
        }

        // Content Column
        Column(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stopName,
                    fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Medium,
                    color = if (isPassed) Color.Gray else Color.Black,
                    fontSize = 16.sp
                )
                
                if (isCurrent) {
                    Spacer(Modifier.width(8.dp))
                    BusIndicator(busStatus)
                }
            }
            
            if (isCurrent) {
                Text(
                    "Bus is currently here",
                    color = Color(0xFF4CAF50),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun BusIndicator(status: BusStatus) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Surface(
        color = (if (status == BusStatus.DELAYED) Color.Red else Color(0xFF4CAF50)).copy(alpha = alpha),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            "LIVE",
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            color = Color.White,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
