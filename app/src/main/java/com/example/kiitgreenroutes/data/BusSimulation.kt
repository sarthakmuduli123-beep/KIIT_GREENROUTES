package com.example.kiitgreenroutes.data

import com.example.kiitgreenroutes.data.model.Bus
import com.example.kiitgreenroutes.data.model.BusStatus
import com.example.kiitgreenroutes.data.model.Route
import com.example.kiitgreenroutes.data.model.Stop
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.util.*
import kotlin.random.Random

object BusSimulation {
    val stops = listOf(
        Stop("1", "Ravi Talkies", 20.2450, 85.8400),
        Stop("2", "Kalpana Square", 20.2550, 85.8450),
        Stop("3", "Rajmahal Square", 20.2650, 85.8400),
        Stop("4", "AG Square", 20.2750, 85.8350),
        Stop("5", "Acharya Vihar", 20.2950, 85.8300),
        Stop("6", "Jaydev Vihar", 20.3050, 85.8250),
        Stop("7", "Nalco Square", 20.3250, 85.8200),
        Stop("8", "Damana Square", 20.3400, 85.8150),
        Stop("9", "KIIT Square", 20.3588, 85.8122),
        Stop("10", "Campus 3/15", 20.3550, 85.8170),
        Stop("11", "Campus 25", 20.3650, 85.8050)
    )

    private val busNumbers = listOf("12", "31", "35", "53", "55", "45", "61", "66", "77", "71", "15", "11", "10", "38", "43", "EIE")

    val routes = mutableListOf<Route>().apply {
        busNumbers.forEach { num ->
            val startPoint = "Ravi Talkies"
            val endPoint = when (num) {
                "12", "31" -> "Campus 25"
                "35", "53" -> "Campus 3/15"
                "55", "45" -> "Campus 5/6"
                "61", "66" -> "Campus 8/17"
                "77", "71" -> "Campus 14/16"
                "15", "11" -> "Campus 22"
                "10", "38" -> "Campus 1/2"
                "43", "EIE" -> "Campus 25 (Express)"
                else -> "Campus 25"
            }
            
            add(Route(
                id = "${num}_UP",
                number = num,
                name = "$startPoint → $endPoint",
                description = "Campus Shuttle Service",
                stops = stops
            ))
            add(Route(
                id = "${num}_DOWN",
                number = num,
                name = "$endPoint → $startPoint",
                description = "Campus Shuttle Service",
                stops = stops.reversed()
            ))
        }
    }

    fun isServiceActive(): Boolean {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return hour in 7..21 // Extended for testing
    }

    fun getBusUpdates() = flow {
        val activeBuses = mutableMapOf<String, Bus>()
        
        while (true) {
            if (!isServiceActive()) {
                emit(emptyList<Bus>())
            } else {
                routes.forEach { route ->
                    val busId = "bus_${route.id}"
                    val currentBus = activeBuses[busId]
                    
                    if (currentBus == null) {
                        val startIndex = 0
                        val startStop = route.stops[startIndex]
                        activeBuses[busId] = Bus(
                            id = busId,
                            routeId = route.id,
                            busNumber = route.number,
                            latitude = startStop.latitude,
                            longitude = startStop.longitude,
                            lastUpdated = System.currentTimeMillis(),
                            status = BusStatus.AT_SOURCE,
                            currentStopIndex = startIndex,
                            etaMinutes = Random.nextInt(2, 10)
                        )
                    } else {
                        val nextIndex = (currentBus.currentStopIndex + 1) % route.stops.size
                        val nextStop = route.stops[nextIndex]
                        
                        val newStatus = when {
                            nextIndex == 0 -> BusStatus.AT_SOURCE
                            nextIndex == route.stops.size - 1 -> BusStatus.REACHED
                            Random.nextInt(10) > 8 -> BusStatus.DELAYED
                            else -> BusStatus.EN_ROUTE
                        }

                        activeBuses[busId] = currentBus.copy(
                            latitude = nextStop.latitude,
                            longitude = nextStop.longitude,
                            lastUpdated = System.currentTimeMillis(),
                            currentStopIndex = nextIndex,
                            status = newStatus,
                            etaMinutes = if (newStatus == BusStatus.DELAYED) Random.nextInt(15, 30) else Random.nextInt(2, 12)
                        )
                    }
                }
                emit(activeBuses.values.toList())
            }
            delay(5000) // Update every 5 seconds
        }
    }
}
