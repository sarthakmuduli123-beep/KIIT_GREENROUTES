package com.example.kiitgreenroutes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kiitgreenroutes.ui.navigation.NavKey

@Composable
fun MainContainer(
    initialTab: Int = 0,
    targetBusId: String? = null,
    onNavigateToRouteDetails: (String) -> Unit,
    onNavigateToHelpAI: () -> Unit,
    onNavigateToProfileEdit: () -> Unit,
    onNavigateToTimetable: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    var currentTrackingBusId by remember { mutableStateOf(targetBusId) }

    LaunchedEffect(initialTab, targetBusId) {
        selectedTab = initialTab
        currentTrackingBusId = targetBusId
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 16.dp, // Added strong shadow
                color = Color.White
            ) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color(0xFF4CAF50),
                    windowInsets = WindowInsets.navigationBars
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
                        label = { Text("Home", fontWeight = if(selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF4CAF50),
                            selectedTextColor = Color(0xFF4CAF50),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color(0xFFE8F5E9)
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Rounded.Map, contentDescription = "Map") },
                        label = { Text("Map", fontWeight = if(selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF4CAF50),
                            selectedTextColor = Color(0xFF4CAF50),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color(0xFFE8F5E9)
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Rounded.Badge, contentDescription = "Pass") },
                        label = { Text("Pass", fontWeight = if(selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF4CAF50),
                            selectedTextColor = Color(0xFF4CAF50),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color(0xFFE8F5E9)
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Icon(Icons.Rounded.Warning, contentDescription = "SOS") },
                        label = { Text("SOS", fontWeight = if(selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF4CAF50),
                            selectedTextColor = Color(0xFF4CAF50),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color(0xFFE8F5E9)
                        )
                    )
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        icon = { Icon(Icons.Rounded.Person, contentDescription = "Profile") },
                        label = { Text("Profile", fontWeight = if(selectedTab == 4) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF4CAF50),
                            selectedTextColor = Color(0xFF4CAF50),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color(0xFFE8F5E9)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = padding.calculateBottomPadding())) {
            when (selectedTab) {
                0 -> HomeScreen(
                    onNavigateToMap = { selectedTab = 1 },
                    onNavigateToRoutes = { busNum -> 
                        onNavigateToRouteDetails(busNum) 
                    },
                    onNavigateToSOS = { selectedTab = 3 },
                    onNavigateToPass = { selectedTab = 2 },
                    onNavigateToSettings = { selectedTab = 4 },
                    onNavigateToTimetable = onNavigateToTimetable
                )
                1 -> MapScreen(
                    initialBusId = currentTrackingBusId,
                    onBack = { 
                        selectedTab = 0 
                        currentTrackingBusId = null
                    }
                )
                2 -> BusPassScreen(onBack = { selectedTab = 0 })
                3 -> SOSScreen(onBack = { selectedTab = 0 })
                4 -> SettingsScreen(
                    onLogout = { /* Handle logout */ },
                    onNavigateToProfile = onNavigateToProfileEdit,
                    onNavigateToHelp = onNavigateToHelpAI
                )
            }
        }
    }
}
