package com.example.kiitgreenroutes.data.model

import java.util.Date

enum class PassStatus {
    ACTIVE,
    EXPIRED,
    PENDING,
    REJECTED
}

data class BusPass(
    val id: String,
    val studentName: String,
    val rollNumber: String,
    val branch: String,
    val expiryDate: Date,
    val status: PassStatus,
    val depositAmount: Double,
    val deviceId: String
)
