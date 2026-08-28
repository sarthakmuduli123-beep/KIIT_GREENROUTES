package com.example.kiitgreenroutes.data.remote

import com.example.kiitgreenroutes.data.model.Bus
import com.example.kiitgreenroutes.data.model.Route
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BusApiService {
    @GET("buses")
    suspend fun getBuses(): List<Bus>

    @GET("buses/{busId}")
    suspend fun getBusDetails(@Path("busId") busId: String): Bus

    @GET("routes")
    suspend fun getRoutes(): List<Route>

    @GET("tracking/{routeId}")
    suspend fun getBusTrackingForRoute(@Path("routeId") routeId: String): List<Bus>
}
