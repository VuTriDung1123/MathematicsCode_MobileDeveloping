package com.uth.elearning.elearningproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uth.elearning.elearningproject.algorithms.*
import com.uth.elearning.elearningproject.screens.AlgorithmsListScreen
import com.uth.elearning.elearningproject.screens.OnboardingScreen


// ---------------- ROUTES ----------------
object AppRoutes {
    const val ONBOARDING_SCREEN = "onboarding"
    const val ALGORITHMS_LIST_SCREEN = "algorithms_list"
    const val SQUAREROOT_SCREEN = "squareroot_screen"
    const val PRIMNUMBER_SCREEN = "primnumber_screen"
    const val PYTHAGOREANTRIPLES_SCREEN = "pythagoreantriples_screen"
    const val EARTHSCIRCUMFERENCE_SCREEN = "earthscircuference_screen"
    const val FIBONACCI_SCREEN = "fibonacci_screen"
    const val SHORTESTDISTANCE_SCREEN= "shortestdistance_screen"
    const val ENCRYPTIONHILLCIPHER_SCREEN = "encryptionhillcipher_screen"
    const val ONEDIMENSIONALRANDOM_SCREEN = "onedimensional_screen"
}

// ---------------- MAIN ACTIVITY ----------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = AppRoutes.ONBOARDING_SCREEN
                ) {
                    composable(AppRoutes.ONBOARDING_SCREEN) {
                        OnboardingScreen(onNavigate = {
                            navController.navigate(AppRoutes.ALGORITHMS_LIST_SCREEN)
                        })
                    }
                    composable(AppRoutes.ALGORITHMS_LIST_SCREEN) {
                        AlgorithmsListScreen(navController)
                    }

                    // Các Routes của Thuật toán (Algorithms)
                    composable(AppRoutes.SQUAREROOT_SCREEN) { SquareRootScreen(navController) }
                    composable(AppRoutes.PYTHAGOREANTRIPLES_SCREEN) { PythagoreanTriplesScreen(navController) }
                    composable(AppRoutes.PRIMNUMBER_SCREEN) { PrimNumberScreen(navController) }
                    composable(AppRoutes.EARTHSCIRCUMFERENCE_SCREEN) { EarthsCircumferenceScreen(navController) }
                    composable(AppRoutes.FIBONACCI_SCREEN) { FibonacciSequenceScreen(navController) }
                    composable(AppRoutes.SHORTESTDISTANCE_SCREEN) { ShortestDistanceScreen(navController) }
                    composable(AppRoutes.ENCRYPTIONHILLCIPHER_SCREEN) { EncryptionHillCipherScreen(navController) }
                    composable(AppRoutes.ONEDIMENSIONALRANDOM_SCREEN) { OneDimensionalRandomScreen(navController) }
                }
            }
        }
    }
}