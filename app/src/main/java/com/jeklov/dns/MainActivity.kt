package com.jeklov.dns

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.jeklov.dns.ui.NavMenuUI
import com.jeklov.dns.ui.theme.DNSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DNSTheme {
                WindowCompat.getInsetsController(window, LocalView.current).apply {
                    isAppearanceLightStatusBars = true // true = темные иконки
                    isAppearanceLightNavigationBars = true // темные иконки в нав-баре
                }
                NavMenuUI(
                    context = this,
                    application = application,
                )
            }
        }
    }
}