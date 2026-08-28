package com.example.kiitgreenroutes.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(onBack: () -> Unit) {
    var name by remember { mutableStateOf("Student Name") }
    var email by remember { mutableStateOf("student@kiit.ac.in") }
    var phone by remember { mutableStateOf("+91 00000 00000") }
    var rollNo by remember { mutableStateOf("220XXXX") }
    var branch by remember { mutableStateOf("Computer Science") }
    
    val branches = listOf("Computer Science", "Electronics (EIE)", "Electrical", "Mechanical", "Civil", "IT")
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Student Profile", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = onBack) {
                        Text("Save", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Premium Avatar Section
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(130.dp),
                    shape = CircleShape,
                    color = Color(0xFFF5F5F5),
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        Icons.Rounded.Person, 
                        contentDescription = null, 
                        modifier = Modifier.padding(30.dp), 
                        tint = Color.Gray
                    )
                }
                FloatingActionButton(
                    onClick = { /* Photo Picker */ },
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Rounded.CameraAlt, contentDescription = "Edit", modifier = Modifier.size(20.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    EditField(label = "Full Name", value = name, onValueChange = { name = it }, icon = Icons.Rounded.Person)
                    EditField(label = "Student Email", value = email, onValueChange = { email = it }, icon = Icons.Rounded.Email)
                    EditField(label = "Roll Number", value = rollNo, onValueChange = { rollNo = it }, icon = Icons.Rounded.Badge)
                    
                    // Branch Dropdown
                    Text(text = "Engineering Branch", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = branch,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF4CAF50),
                                unfocusedBorderColor = Color.LightGray,
                                shape = RoundedCornerShape(12.dp)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            branches.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        branch = selectionOption
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(24.dp))
            
            // Student ID Card Preview
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF1A237E).copy(alpha = 0.05f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1A237E).copy(alpha = 0.1f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Info, contentDescription = null, tint = Color(0xFF1A237E))
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Providing correct branch details helps us optimize shuttle schedules for your specific campus.",
                        fontSize = 12.sp,
                        color = Color(0xFF1A237E)
                    )
                }
            }
        }
    }
}

@Composable
fun EditField(label: String, value: String, onValueChange: (String) -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color(0xFF4CAF50)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4CAF50),
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
    }
}
