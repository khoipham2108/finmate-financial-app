package com.example.finmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.finmate.ui.FinMateNavHost
import com.example.finmate.ui.theme.FinMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinMateTheme {
                FinMateNavHost()
            }
        }
    }
}
