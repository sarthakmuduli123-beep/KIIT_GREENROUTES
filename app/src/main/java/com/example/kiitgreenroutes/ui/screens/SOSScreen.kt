package com.example.kiitgreenroutes.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kiitgreenroutes.data.model.UserSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOSScreen(onBack: () -> Unit) {
    var isSending by remember { mutableStateOf(false) }
    var alertSent by remember { mutableStateOf(false) }
    var emergencyType by remember { mutableIntStateOf(0) } // 0: Security, 1: Medical
    val scope = rememberCoroutineScope()
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Campus Safety & SOS", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Emergency Type Toggle
            EmergencyToggle(
                selected = emergencyType,
                onSelected = { emergencyType = it }
            )

            Spacer(Modifier.height(40.dp))

            // SOS Main Button
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .scale(if (isSending) 1f else pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            if (alertSent)
                                listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))
                            else
                                if (emergencyType == 0)
                                    listOf(Color(0xFFFF5252), Color(0xFFFF1744))
                                else
                                    listOf(Color(0xFF2196F3), Color(0xFF1976D2))
                        )
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                scope.launch {
                                    isSending = true
                                    delay(2000)
                                    isSending = false
                                    alertSent = true
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (alertSent) Icons.Rounded.CheckCircle 
                                      else if (emergencyType == 0) Icons.Rounded.Shield 
                                      else Icons.Rounded.MedicalServices,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = if (alertSent) "SENT" else if (isSending) "SENDING..." else "HOLD SOS",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = if (alertSent)
                    "Emergency Services Notified!\nDispatcher: KIMS Central Unit"
                else
                    "Press and hold for 2 seconds to alert ${if (emergencyType == 0) "Campus Security" else "KIMS Medical Unit"}",
                textAlign = TextAlign.Center,
                color = if (alertSent) Color(0xFF2E7D32) else Color.Gray,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(40.dp))

            // Quick Contact Grid
            Text(
                "Quick Emergency Contacts",
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(16.dp))
            EmergencyContactGrid()

            Spacer(Modifier.height(30.dp))

            // Location Details
            LocationCard()
        }
    }
}

@Composable
fun EmergencyToggle(selected: Int, onSelected: (Int) -> Unit) {
    Surface(
        color = Color(0xFFE9ECEF),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            ToggleButton(
                text = "Security",
                icon = Icons.Rounded.Security,
                isSelected = selected == 0,
                modifier = Modifier.weight(1f),
                selectedColor = Color(0xFFFF5252),
                onClick = { onSelected(0) }
            )
            ToggleButton(
                text = "Medical",
                icon = Icons.Rounded.HealthAndSafety,
                isSelected = selected == 1,
                modifier = Modifier.weight(1f),
                selectedColor = Color(0xFF2196F3),
                onClick = { onSelected(1) }
            )
        }
    }
}

@Composable
fun ToggleButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) Color.White else Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.clickable { onClick() },
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = if (isSelected) selectedColor else Color.Gray, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(text, fontWeight = FontWeight.Bold, color = if (isSelected) Color.Black else Color.Gray)
        }
    }
}

@Composable
fun EmergencyContactGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            EmergencyActionCard(Icons.Rounded.LocalPolice, "Police", Color(0xFF3F51B5), Modifier.weight(1f), {})
            EmergencyActionCard(Icons.Rounded.MedicalInformation, "KIMS Help", Color(0xFF4CAF50), Modifier.weight(1f), {})
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            EmergencyActionCard(Icons.Rounded.RecordVoiceOver, "Guardian", Color(0xFF607D8B), Modifier.weight(1f), {})
            EmergencyActionCard(Icons.Rounded.ShareLocation, "Share Trip", Color(0xFFFF9800), Modifier.weight(1f), {})
        }
    }
}

@Composable
fun EmergencyActionCard(icon: ImageVector, label: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
fun LocationCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFE8F5E9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.MyLocation, null, tint = Color(0xFF4CAF50))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Your Live Location", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                Text("KIIT Campus 6, Near Gate 2", fontSize = 13.sp, color = Color.Gray)
                Text("User: ${UserSession.rollNumber}", fontSize = 11.sp, color = Color.LightGray)
            }
        }
    }
}
