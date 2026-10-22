package com.example.connective

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.connective.data.ConnectiveRepository
import com.example.connective.navigation.AppNavigation
import com.example.connective.ui.theme.CONNECTIVETheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ConnectiveRepository.initialize(applicationContext)
        enableEdgeToEdge()
        setContent {
            CONNECTIVETheme {
                AppNavigation()
            }
        }
    }
}