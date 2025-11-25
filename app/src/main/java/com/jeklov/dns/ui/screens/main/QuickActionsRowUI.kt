package com.jeklov.dns.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeklov.dns.R

data class QuickActionItem(
    val title: String,
    val imageRes: Int,      // ID ресурса картинки (drawable)
    val isNew: Boolean = false // Флаг для бейджика "Новое"
)

@Composable
fun QuickActionsSection() {
    val items = listOf(
        QuickActionItem(
            title = "Скидки и\nакции",
            imageRes = R.drawable.ic_home_promotions, // Ваша картинка %
            isNew = false
        ),
        QuickActionItem(
            title = "Статус заказа",
            imageRes = R.drawable.ic_home_orders, // Ваша картинка коробки
            isNew = false
        ),
        QuickActionItem(
            title = "DNS Shorts",
            imageRes = R.drawable.ic_home_shorts, // Ваша картинка Play
            isNew = true
        ),
        QuickActionItem(
            title = "Магазины на карте",
            imageRes = R.drawable.ic_home_shops,
            isNew = false
        ),
        QuickActionItem(
            title = "Собрать\nПК",
            imageRes = R.drawable.ic_home_configurator,
            isNew = false
        ),
        QuickActionItem(
            title = "Готовые\nсборки",
            imageRes = R.drawable.ic_home_rsu,
            isNew = false
        ),
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            QuickActionCardTemplate(item)
        }
    }
}

@Composable
fun QuickActionCardTemplate(item: QuickActionItem) {
    Card(
        onClick = { /* Action */ },
        colors = CardDefaults.cardColors(containerColor = Color.White), // Белый фон как на макете
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Легкая тень
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.size(width = 92.dp, height = 106.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Заголовок (сверху слева)
            Text(
                text = item.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Light,
                lineHeight = 18.sp,
                color = Color.Black,
                modifier = Modifier
                    .padding(top = 10.dp, start = 8.dp, end = 8.dp)
                    .align(Alignment.TopStart)
            )

            // Бейджик "Новое" (опционально, сверху справа)
            if (item.isNew) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(3.dp)
                        .height(12.dp)
                        .background(Color(0xFF4CAF50), RoundedCornerShape(14.dp))
                        .padding(horizontal = 2.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "Новое",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 4.sp
                    )
                }
            }

            // Картинка (снизу справа)
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(52.dp) // Размер картинки
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 8.dp, end = 8.dp)
            )
        }
    }
}