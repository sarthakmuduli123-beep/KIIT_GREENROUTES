package com.example.kiitgreenroutes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToProfile: () -> Unit = {},
    onNavigateToHelp: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showReportDialog by remember { mutableStateOf(false) }
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            LargeTopAppBar(
                title = { 
                    Text(
                        "Settings", 
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A237E)
                    ) 
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA)),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                PremiumProfileCard(onClick = onNavigateToProfile)
            }
            
            item {
                SectionHeader("Preferences")
            }

            items(getSettingsItems().take(3)) { item ->
                PremiumSettingsItem(item, onClick = {
                    when (item.title) {
                        "Profile & Account" -> onNavigateToProfile()
                        "App Preferences" -> {
                            scope.launch { snackbarHostState.showSnackbar("Preference controls coming soon!") }
                        }
                    }
                })
            }

            item {
                SectionHeader("Support & Info")
            }

            items(getSettingsItems().drop(3)) { item ->
                PremiumSettingsItem(item, onClick = {
                    when (item.title) {
                        "Help & Support" -> onNavigateToHelp()
                        "Report an Issue" -> showReportDialog = true
                        "App Version" -> {
                            scope.launch { 
                                snackbarHostState.showSnackbar("You are on the latest version (v1.0.0)")
                            }
                        }
                        "Privacy Policy" -> {
                            uriHandler.openUri("https://kiit.ac.in/privacy-policy") // Example
                        }
                    }
                })
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFEBEE), 
                        contentColor = Color(0xFFD32F2F)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Logout Session", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (showReportDialog) {
        ReportIssueDialog(onDismiss = { showReportDialog = false })
    }
}

@Composable
fun ReportIssueDialog(onDismiss: () -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report an Issue", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Describe the problem you're facing on campus.", fontSize = 14.sp, color = Color.Gray)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("e.g. Bus 12 is not showing on map...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Submit Report")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
fun PremiumProfileCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF1A237E), Color(0xFF4CAF50))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "S", 
                    color = Color.White, 
                    fontSize = 28.sp, 
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Student Name", 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
                Text(
                    "student@kiit.ac.in", 
                    color = Color.Gray, 
                    fontSize = 14.sp
                )
                
                Spacer(Modifier.height(4.dp))
                
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Verified Student",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
            
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight, 
                contentDescription = null, 
                tint = Color.LightGray
            )
        }
    }
}

@Composable
fun PremiumSettingsItem(item: SettingsItem, onClick: () -> Unit) {
    var checked by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF1F3F4)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        item.icon, 
                        contentDescription = null, 
                        tint = Color(0xFF1A237E), 
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                item.title, 
                modifier = Modifier.weight(1f), 
                fontSize = 16.sp, 
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )
            
            if (item.title == "Notification Preferences") {
                Switch(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF4CAF50)
                    )
                )
            } else if (item.showBadge) {
                Text(
                    "v1.0.0", 
                    color = Color.Gray, 
                    fontSize = 12.sp, 
                    modifier = Modifier.padding(end = 8.dp)
                )
            } else {
                Icon(
                    Icons.AutoMirrored.Rounded.KeyboardArrowRight, 
                    contentDescription = null, 
                    tint = Color.LightGray
                )
            }
        }
    }
}

data class SettingsItem(val icon: ImageVector, val title: String, val showBadge: Boolean = false)

fun getSettingsItems() = listOf(
    SettingsItem(Icons.Rounded.AccountCircle, "Profile & Account"),
    SettingsItem(Icons.Rounded.NotificationsNone, "Notification Preferences"),
    SettingsItem(Icons.Rounded.AppSettingsAlt, "App Preferences"),
    SettingsItem(Icons.AutoMirrored.Rounded.Help, "Help & Support"),
    SettingsItem(Icons.Rounded.BugReport, "Report an Issue"),
    SettingsItem(Icons.Rounded.Info, "App Version", showBadge = true),
    SettingsItem(Icons.Rounded.PrivacyTip, "Privacy Policy")
)
