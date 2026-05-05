package com.healthlog.myapplication1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.healthlog.myapplication1.ui.navigation.AppNavigation
import com.healthlog.myapplication1.ui.theme.BgBase
import com.healthlog.myapplication1.ui.theme.HealthLogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthLogTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgBase
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
