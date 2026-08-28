package com.example.kiitgreenroutes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey val id: String,
    val number: String,
    val name: String,
    val description: String
)

@Entity(tableName = "stops")
data class StopEntity(
    @PrimaryKey val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double
)

@Entity(tableName = "route_stops", primaryKeys = ["routeId", "stopId"])
data class RouteStopCrossRef(
    val routeId: String,
    val stopId: String,
    val order: Int
)
