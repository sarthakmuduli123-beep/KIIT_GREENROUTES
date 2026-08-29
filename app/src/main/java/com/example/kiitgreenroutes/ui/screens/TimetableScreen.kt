package com.example.kiitgreenroutes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kiitgreenroutes.data.BusSimulation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    onBack: () -> Unit,
    onRouteClick: (String) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(
                color = Color.White,
                tonalElevation = 8.dp,
                shadowElevation = 4.dp
            ) {
                TopAppBar(
                    title = { Text("KIIT Bus Timetable", fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A237E)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                    modifier = Modifier.statusBarsPadding()
                )
            }
        }
    ) { padding ->
        val groupedRoutes = BusSimulation.routes.groupBy { it.number }
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(top = padding.calculateTopPadding())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(10.dp))
                Text(
                    "Select a Bus to View Route",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            groupedRoutes.forEach { (busNumber, routes) ->
                item {
                    BusGroupCard(
                        busNumber = busNumber,
                        routes = routes,
                        onRouteClick = onRouteClick
                    )
                }
            }
            
            item {
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun BusGroupCard(busNumber: String, routes: List<com.example.kiitgreenroutes.data.model.Route>, onRouteClick: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFF4CAF50), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.DirectionsBus, contentDescription = null, tint = Color.White)
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "Bus No. $busNumber",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2D3436)
                )
            }
            
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            Spacer(Modifier.height(8.dp))
            
            routes.forEach { route ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRouteClick(route.id) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (route.id.contains("UP")) Icons.Rounded.ArrowUpward else Icons.Rounded.ArrowDownward,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = route.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.Rounded.ChevronRight,
                        contentDescription = null,
                        tint = Color.LightGray
                    )
                }
            }
        }
    }
}
