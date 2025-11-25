package com.jeklov.dns.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.jeklov.dns.R
import com.jeklov.dns.ui.screens.Screens

@Composable
fun TopCityAndDiscountsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Город
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_home_location),
                contentDescription = "Location",
                tint = Color(0xFF266CCB),
                modifier = Modifier.size(23.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Москва",
                color = Color(0xFF266CCB),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Скидки
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Скидки",
                color = Color(0xFFCC330D),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                painter = painterResource(R.drawable.ic_home_fire),
                contentDescription = "Discounts",
                tint = Color.Unspecified,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun SearchBarHeader(
    navigationController: NavHostController,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Поле поиска
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF5F5F5))
                    .clickable { navigationController.navigate(Screens.SearchPage.screen) },
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_home_search),
                        contentDescription = "Search",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Искать в DNS",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )

                    Icon(
                        painter = painterResource(R.drawable.ic_home_barcode),
                        contentDescription = "Scan",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(CornerSize(4.dp)))
                            .clickable {}
                    )

                    Box(modifier = Modifier.width(10.dp))

                    Icon(
                        painter = painterResource(R.drawable.ic_ai_assistant),
                        contentDescription = "AI",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(CornerSize(4.dp)))
                            .clickable { navigationController.navigate(Screens.AssistantPage.screen + "/") }
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Колокольчик
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF5F5F5))
                    .clickable { /* Уведомления */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_home_notifications),
                    contentDescription = "Notifications",
                    tint = Color.Gray
                )
            }
        }

        // Тень
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}