package com.example.kiitgreenroutes.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.kiitgreenroutes.ui.navigation.NavKey

@Composable
fun MainContainer(
    onNavigateToRouteDetails: (String) -> Unit,
    onNavigateToHelpAI: () -> Unit,
    onNavigateToProfileEdit: () -> Unit,
    onNavigateToTimetable: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = Color(0xFF4CAF50)
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF4CAF50),
                        selectedTextColor = Color(0xFF4CAF50),
                        indicatorColor = Color(0xFFE8F5E9)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Rounded.Map, contentDescription = "Map") },
                    label = { Text("Map") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF4CAF50),
                        selectedTextColor = Color(0xFF4CAF50),
                        indicatorColor = Color(0xFFE8F5E9)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Rounded.Badge, contentDescription = "Pass") },
                    label = { Text("Pass") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF4CAF50),
                        selectedTextColor = Color(0xFF4CAF50),
                        indicatorColor = Color(0xFFE8F5E9)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Rounded.Warning, contentDescription = "SOS") },
                    label = { Text("SOS") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF4CAF50),
                        selectedTextColor = Color(0xFF4CAF50),
                        indicatorColor = Color(0xFFE8F5E9)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Rounded.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF4CAF50),
                        selectedTextColor = Color(0xFF4CAF50),
                        indicatorColor = Color(0xFFE8F5E9)
                    )
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
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
                1 -> MapScreen(onBack = { selectedTab = 0 })
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
