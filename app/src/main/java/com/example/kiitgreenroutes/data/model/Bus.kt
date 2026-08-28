package com.example.kiitgreenroutes.data.model

import kotlinx.serialization.Serializable

enum class BusStatus {
    AT_SOURCE,    // Inside Campus/Starting point
    EN_ROUTE,     // On the road
    DELAYED,      // Stuck in traffic
    REACHED       // At destination
}

@Serializable
data class Bus(
    val id: String,
    val routeId: String,
    val busNumber: String,
    val latitude: Double,
    val longitude: Double,
    val lastUpdated: Long,
    val currentStopId: String? = null,
    val nextStopId: String? = null,
    val occupancy: Int = 0, // 0-100 percentage
    val status: BusStatus = BusStatus.AT_SOURCE,
    val currentStopIndex: Int = 0,
    val etaMinutes: Int = 5
)
