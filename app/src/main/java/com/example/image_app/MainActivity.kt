package com.example.image_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.image_app.Screens.ImageScreen
import com.example.image_app.ui.theme.Image_appTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
                installSplashScreen()
        setContent {
            Image_appTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ImageScreen(navController = rememberNavController())

                }
            }
        }
    }
}
