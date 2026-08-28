package com.example.kiitgreenroutes.data.model

object UserSession {
    var userEmail: String? = null
    var userName: String? = null
    
    val rollNumber: String
        get() = userEmail?.substringBefore("@") ?: "N/A"
}
