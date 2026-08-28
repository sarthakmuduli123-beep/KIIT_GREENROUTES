package com.example.kiitgreenroutes.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Stop(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double
)
