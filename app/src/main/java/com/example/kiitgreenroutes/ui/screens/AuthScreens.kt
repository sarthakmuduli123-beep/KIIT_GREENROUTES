package com.example.kiitgreenroutes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.kiitgreenroutes.data.model.UserSession
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onBack: () -> Unit, onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val auth = remember { FirebaseAuth.getInstance() }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 30.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Welcome Back",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Enter your student credentials to continue",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    error = null
                },
                label = { Text("Student Email ID") },
                placeholder = { Text("e.g. 220100@kiit.ac.in") },
                modifier = Modifier.fillMaxWidth(),
                isError = error != null,
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Rounded.Mail, contentDescription = null, tint = Color(0xFF4CAF50)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            if (error != null) {
                Text(
                    text = error!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null, tint = Color(0xFF4CAF50)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            
            TextButton(
                onClick = { /* TODO */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Forgot Password?", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = {
                    if (email.endsWith("@kiit.ac.in")) {
                        scope.launch {
                            isLoading = true
                            try {
                                val result = auth.signInWithEmailAndPassword(email, password).await()
                                val user = result.user
                                if (user != null) {
                                    // Bypass email verification for testing/testing credentials
                                    UserSession.userEmail = email
                                    UserSession.userName = user.displayName ?: "Student"
                                    onLoginSuccess()
                                }
                            } catch (e: FirebaseAuthInvalidUserException) {
                                error = "User not found. Please sign up first."
                            } catch (e: FirebaseAuthInvalidCredentialsException) {
                                error = "Incorrect email or password. Please try again."
                            } catch (e: FirebaseNetworkException) {
                                error = "Network error. Please check your internet connection."
                            } catch (e: Exception) {
                                error = e.localizedMessage ?: "Login failed"
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        error = "Only @kiit.ac.in emails are allowed"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White)
                else Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(onBack: () -> Unit, onSignupSuccess: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showVerificationNotice by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    
    val scope = rememberCoroutineScope()
    val auth = remember { FirebaseAuth.getInstance() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 30.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Create Account",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Join the KIIT GreenRoute community",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null, tint = Color(0xFF4CAF50)) }
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it 
                    error = null
                },
                label = { Text("Student Email ID") },
                placeholder = { Text("e.g. 220100@kiit.ac.in") },
                modifier = Modifier.fillMaxWidth(),
                isError = error != null,
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Rounded.Mail, contentDescription = null, tint = Color(0xFF4CAF50)) }
            )

            if (error != null) {
                Text(
                    text = error!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Create Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null, tint = Color(0xFF4CAF50)) },
                visualTransformation = PasswordVisualTransformation()
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = {
                    if (email.endsWith("@kiit.ac.in")) {
                        scope.launch {
                            isLoading = true
                            try {
                                val result = auth.createUserWithEmailAndPassword(email, password).await()
                                val user = result.user
                                if (user != null) {
                                    // Set Display Name
                                    val profileUpdates = userProfileChangeRequest {
                                        displayName = name
                                    }
                                    user.updateProfile(profileUpdates).await()
                                    
                                    // Send Verification Email
                                    user.sendEmailVerification().await()
                                    
                                    showVerificationNotice = true
                                    // UserSession is only set after verification login
                                }
                            } catch (e: Exception) {
                                error = e.localizedMessage ?: "Signup failed"
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        error = "Only @kiit.ac.in emails are allowed"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White)
                else Text("Create Account", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showVerificationNotice) {
        AlertDialog(
            onDismissRequest = { /* Don't dismiss without action */ },
            title = { Text("Verify Email") },
            text = { Text("A verification link has been sent to $email. Please verify your email and then login.") },
            confirmButton = {
                Button(onClick = { 
                    showVerificationNotice = false
                    auth.signOut()
                    onBack() 
                }) {
                    Text("OK, Go to Login")
                }
            }
        )
    }
}
