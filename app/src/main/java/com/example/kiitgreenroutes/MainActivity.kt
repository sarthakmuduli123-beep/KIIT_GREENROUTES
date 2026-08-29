package com.example.kiitgreenroutes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.kiitgreenroutes.ui.navigation.NavKey
import com.example.kiitgreenroutes.ui.screens.*
import com.example.kiitgreenroutes.ui.theme.KIITGREENROUTESTheme
import androidx.navigation3.runtime.NavKey as BaseNavKey

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KIITGREENROUTESTheme {
                val backStack = rememberNavBackStack(NavKey.Splash as BaseNavKey)
                
                NavDisplay(
                    backStack = backStack,
                    onBack = { 
                        if (backStack.size > 1) {
                            backStack.removeAt(backStack.size - 1)
                        }
                    },
                    entryProvider = entryProvider {
                        entry<NavKey.Splash> {
                            PremiumSplashScreen(onSplashFinished = {
                                backStack.removeAt(backStack.size - 1)
                                backStack.add(NavKey.Landing)
                            })
                        }

                        entry<NavKey.Instruction> {
                            InstructionScreen(onContinue = {
                                backStack.removeAt(backStack.size - 1)
                                backStack.add(NavKey.Home)
                            })
                        }

                        entry<NavKey.Landing> {
                            LandingScreen(
                                onLoginClick = { backStack.add(NavKey.Login) },
                                onCreateAccountClick = { backStack.add(NavKey.Signup) },
                                onContinueAsGuestClick = { backStack.add(NavKey.Home) }
                            )
                        }
                        
                        entry<NavKey.Login> {
                            LoginScreen(
                                onBack = { backStack.removeAt(backStack.size - 1) },
                                onLoginSuccess = { 
                                    backStack.removeAt(backStack.size - 1)
                                    backStack.add(NavKey.Instruction) 
                                }
                            )
                        }
                        
                        entry<NavKey.Signup> {
                            SignupScreen(
                                onBack = { backStack.removeAt(backStack.size - 1) },
                                onSignupSuccess = {
                                    backStack.removeAt(backStack.size - 1)
                                    backStack.add(NavKey.Instruction)
                                }
                            )
                        }

                        entry<NavKey.Home> {
                            MainContainer(
                                onNavigateToRouteDetails = { routeId -> 
                                    backStack.add(NavKey.RouteDetails(routeId)) 
                                },
                                onNavigateToHelpAI = { backStack.add(NavKey.HelpAI) },
                                onNavigateToProfileEdit = { backStack.add(NavKey.ProfileEdit) },
                                onNavigateToTimetable = { backStack.add(NavKey.Timetable) }
                            )
                        }
                        
                        entry<NavKey.BusTracking> { tracking ->
                            MainContainer(
                                initialTab = 1,
                                targetBusId = tracking.busId,
                                onNavigateToRouteDetails = { routeId -> 
                                    backStack.add(NavKey.RouteDetails(routeId)) 
                                },
                                onNavigateToHelpAI = { backStack.add(NavKey.HelpAI) },
                                onNavigateToProfileEdit = { backStack.add(NavKey.ProfileEdit) },
                                onNavigateToTimetable = { backStack.add(NavKey.Timetable) }
                            )
                        }

                        entry<NavKey.RouteDetails> { routeDetails ->
                            RouteDetailsScreen(
                                routeId = routeDetails.routeId,
                                onBack = { backStack.removeAt(backStack.size - 1) }
                            )
                        }

                        entry<NavKey.Timetable> {
                            TimetableScreen(
                                onBack = { backStack.removeAt(backStack.size - 1) },
                                onRouteClick = { busNum -> 
                                    backStack.add(NavKey.RouteDetails(busNum))
                                }
                            )
                        }

                        entry<NavKey.HelpAI> {
                            HelpAIScreen(onBack = { backStack.removeAt(backStack.size - 1) })
                        }

                        entry<NavKey.ProfileEdit> {
                            ProfileEditScreen(onBack = { backStack.removeAt(backStack.size - 1) })
                        }
                    }
                )
            }
        }
    }
}
