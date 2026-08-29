package com.example.kiitgreenroutes.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.Bitmap
import com.example.kiitgreenroutes.data.model.BusPass
import com.example.kiitgreenroutes.data.model.PassStatus
import com.example.kiitgreenroutes.data.model.UserSession
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusPassScreen(onBack: () -> Unit) {
    // Simulated Student Pass Data derived from UserSession
    val studentPass = remember {
        BusPass(
            id = "PASS-2024-KIIT-${(1000..9999).random()}",
            studentName = UserSession.userName ?: "KIIT Student",
            rollNumber = UserSession.rollNumber,
            branch = "KIIT University",
            expiryDate = Calendar.getInstance().apply { add(Calendar.MONTH, 6) }.time,
            status = PassStatus.ACTIVE,
            depositAmount = 5000.0,
            deviceId = "Device-Verified"
        )
    }

    var dynamicToken by remember { mutableStateOf(generateToken(studentPass.rollNumber)) }
    
    // Refresh token every 15 seconds for security (Anti-screenshot)
    LaunchedEffect(Unit) {
        while (true) {
            delay(15000)
            dynamicToken = generateToken(studentPass.rollNumber)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(
                color = Color.White,
                tonalElevation = 8.dp,
                shadowElevation = 4.dp
            ) {
                TopAppBar(
                    title = { Text("KIIT Bus Pass", fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A237E)) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
                .padding(top = padding.calculateTopPadding())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live Status Indicator
            LiveStatusBanner(studentPass.status)
            
            Spacer(Modifier.height(20.dp))

            // Main Pass Card
            PassCard(studentPass, dynamicToken)

            Spacer(Modifier.height(24.dp))

            // Deposit Information
            DepositDetails(studentPass)

            Spacer(Modifier.height(30.dp))
            
            // Verification Notice
            InfoNotice()
        }
    }
}

private fun generateToken(roll: String): String {
    val timestamp = System.currentTimeMillis() / 15000 // Consistent for 15 sec
    return "KIIT|${roll}|${timestamp}|${(1000..9999).random()}"
}

@Composable
fun LiveStatusBanner(status: PassStatus) {
    val color = when(status) {
        PassStatus.ACTIVE -> Color(0xFF4CAF50)
        else -> Color.Red
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color.copy(alpha = alpha), CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "PASS STATUS: ${status.name}",
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun PassCard(pass: BusPass, token: String) {
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    LaunchedEffect(token) {
        withContext(Dispatchers.Default) {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(token, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            qrBitmap = bmp
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // University Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(Color(0xFF4CAF50), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("K", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("KIIT UNIVERSITY", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    Text("Digital Transport Pass", color = Color.Gray, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Dynamic QR Section
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                qrBitmap?.let {
                    androidx.compose.foundation.Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Bus Pass QR",
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: CircularProgressIndicator(color = Color(0xFF4CAF50))
                
                // Animated Scanning Line (Security feature)
                val infiniteTransition = rememberInfiniteTransition(label = "scan")
                val yOffset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 180f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "y"
                )
                
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawLine(
                        color = Color(0xFF4CAF50).copy(alpha = 0.5f),
                        start = Offset(0f, yOffset.dp.toPx()),
                        end = Offset(size.width, yOffset.dp.toPx()),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = token.takeLast(8), 
                fontSize = 10.sp, 
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            // Student Info
            Column(modifier = Modifier.fillMaxWidth()) {
                InfoRow("Student Name", pass.studentName)
                InfoRow("Roll Number", pass.rollNumber)
                InfoRow("Branch", pass.branch)
                InfoRow("Valid Upto", SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(pass.expiryDate))
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun DepositDetails(pass: BusPass) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFE8F5E9)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = null, tint = Color(0xFF4CAF50))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Last Deposit: ₹${pass.depositAmount.toInt()}", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                Text("Verified by Finance Dept.", fontSize = 12.sp, color = Color(0xFF4CAF50))
            }

            Spacer(Modifier.weight(1f))
            Icon(Icons.Rounded.Verified, contentDescription = null, tint = Color(0xFF4CAF50))
        }
    }
}

@Composable
fun InfoNotice() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Notice: Screenshot verification is not allowed. Show this live screen to the bus conductor.",
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { /* TODO: Refresh */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Gray)
        ) {
            Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("Refresh Pass Data", fontSize = 12.sp)
        }
    }
}
