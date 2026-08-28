package com.example.kiitgreenroutes.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val id: String,
    val number: String,
    val name: String,
    val description: String,
    val stops: List<Stop> = emptyList()
)
