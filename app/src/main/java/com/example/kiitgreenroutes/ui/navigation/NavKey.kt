package com.example.kiitgreenroutes.ui.navigation

import kotlinx.serialization.Serializable
import androidx.navigation3.runtime.NavKey as BaseNavKey

sealed interface NavKey : BaseNavKey {
    @Serializable
    data object Splash : NavKey

    @Serializable
    data object Instruction : NavKey

    @Serializable
    data object Landing : NavKey

    @Serializable
    data object Login : NavKey

    @Serializable
    data object Signup : NavKey

    @Serializable
    data object Home : NavKey

    @Serializable
    data object Map : NavKey

    @Serializable
    data class RouteDetails(val routeId: String) : NavKey

    @Serializable
    data class BusTracking(val busId: String) : NavKey

    @Serializable
    data object BusPass : NavKey

    @Serializable
    data object SOS : NavKey

    @Serializable
    data object Settings : NavKey

    @Serializable
    data object ProfileEdit : NavKey

    @Serializable
    data object Timetable : NavKey

    @Serializable
    data object HelpAI : NavKey
}
