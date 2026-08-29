package com.example.kiitgreenroutes.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun BusTrackingScreen(busId: String) {
    // We reuse MapScreen for tracking, but with a specific bus focus
    // If we want a separate simple screen for "Not Found":
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Tracking Bus: $busId", style = MaterialTheme.typography.headlineSmall)
            Text(text = "Checking live status...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
