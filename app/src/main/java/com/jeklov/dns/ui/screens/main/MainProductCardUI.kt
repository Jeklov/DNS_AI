package com.jeklov.dns.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeklov.dns.R


@Composable
fun ProductCardExact(product: ProductUIModel) {
    Card(
        onClick = { /* Детали товара */ }, // <<< ИСПРАВЛЕНИЕ: Передаем onClick прямо в Card
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // --- 1. Картинка и Бейджик ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                // Картинка товара
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.title,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxHeight()
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )

                // Бейджик (если есть)
                if (product.badgeIconRes != null && product.badgeColor != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .size(32.dp)
                            .background(product.badgeColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = product.badgeIconRes),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- 2. Название ---
            Text(
                text = product.title,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                maxLines = 3,
                minLines = 3, // Фиксируем высоту текста
                overflow = TextOverflow.Ellipsis,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- 3. Рейтинг (в рамке) ---
            Row(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Рисуем звезды с поддержкой дробных чисел
                StaticRatingBar(rating = product.rating)

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "${product.reviewsCount}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- 4. Цена и кнопка ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Блок цены (серая плашка)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF6F6F6), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = product.price,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = product.monthlyPayment,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Круглая кнопка (i) или корзина
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape) // <<< УЛУЧШЕНИЕ: Обрезаем ripple-эффект по кругу
                        .clickable { /* Инфо/Корзина */ }
                        .background(Color(0xFFF6F6F6), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_profile_about), // Или ShoppingCart
                        contentDescription = "Info",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// Вспомогательная функция для отрисовки звезд
@Composable
fun StaticRatingBar(rating: Double) {
    Row {
        for (i in 1..5) {
            val starSize = 14.dp
            val starColor = Color(0xFFFFB800) // Желтый DNS

            if (rating >= i) {
                // Полная звезда
                Icon(
                    painter = painterResource(R.drawable.ic_home_star),
                    contentDescription = null,
                    tint = starColor,
                    modifier = Modifier.size(starSize)
                )
            } else if (rating > i - 1) {
                // Частичная звезда
                val fraction = (rating - (i - 1)).toFloat()
                Box(modifier = Modifier.size(starSize)) {
                    // Фон (серая звезда, опционально, но на белом фоне не видно)
                    Icon(
                        painter = painterResource(R.drawable.ic_home_star),
                        contentDescription = null,
                        tint = Color(0xFFE0E0E0),
                        modifier = Modifier.size(starSize)
                    )
                    // Обрезанная цветная звезда
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(FractionalRectangleShape(0f, fraction))
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_home_star),
                            contentDescription = null,
                            tint = starColor,
                            modifier = Modifier.size(starSize)
                        )
                    }
                }
            } else {
                // Пустая звезда (серый контур или заливка)
                Icon(
                    painter = painterResource(R.drawable.ic_home_star),
                    contentDescription = null,
                    tint = Color(0xFFE0E0E0),
                    modifier = Modifier.size(starSize)
                )
            }
        }
    }
}

// Custom Shape для обрезки звезды
fun FractionalRectangleShape(startFraction: Float, endFraction: Float) = object :
    Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density
    ): androidx.compose.ui.graphics.Outline {
        return androidx.compose.ui.graphics.Outline.Rectangle(
            androidx.compose.ui.geometry.Rect(
                left = startFraction * size.width,
                top = 0f,
                right = endFraction * size.width,
                bottom = size.height
            )
        )
    }
}
