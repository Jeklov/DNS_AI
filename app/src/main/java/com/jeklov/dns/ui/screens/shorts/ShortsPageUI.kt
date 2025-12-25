package com.jeklov.dns.ui.screens.shorts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jeklov.dns.R

@Composable
fun ShortsPageUI(
    paddingValues: PaddingValues,
    navigationController: NavHostController,
    //database: MainDB,
) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            // Иконка "в разработке" или похожая
            Icon(
                painter = painterResource(R.drawable.ic_profile_service_center), // Можно заменить на SentimentDissatisfied или другую
                contentDescription = "Under Construction",
                modifier = Modifier.size(64.dp),
                tint = Color(0xFFFFA500)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Заголовок
            Text(
                text = "Ой!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Основной текст
            Text(
                text = "Простите... Но я всего лишь макет,\nя пока так еще не умею ;(",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
