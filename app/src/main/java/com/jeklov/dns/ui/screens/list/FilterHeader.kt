package com.jeklov.dns.ui.screens.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeklov.dns.R

@Composable
fun FilterHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White) // Белый фон как на скриншоте
            .padding(bottom = 12.dp)
    ) {
        // --- Верхняя строка: Сортировка и Фильтры ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Левая часть: "Сначала популярные" + галочка
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { /* Открыть сортировку */ }
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = "Сначала популярные",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF333333),
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_home_star), // Стандартная иконка стрелки
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Правая часть: Иконка настроек + "Фильтры"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { /* Открыть фильтры */ }
                    .padding(start = 8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_home_star),
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Фильтры",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF333333),
                        fontSize = 14.sp
                    )
                )
            }
        }

        // --- Нижняя строка: Горизонтальный скролл (Чипсы) ---
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // 1. Переключатель вида (Список / Плитка)
            item {
                Surface(
                    color = Color(0xFFF5F5F5), // Светло-серый фон
                    shape = CircleShape, // Полностью скругленные углы (pill shape)
                    modifier = Modifier.height(32.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { /* Переключить вид */ }
                            .padding(horizontal = 12.dp)
                    ) {
                        // Иконка списка (акцентная)
                        Icon(
                            painter = painterResource(R.drawable.ic_home_star),
                            contentDescription = "Список",
                            tint = Color.Black, // Активный цвет
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Иконка плитки (серая)
                        Icon(
                            painter = painterResource(R.drawable.ic_home_star),
                            contentDescription = "Плитка",
                            tint = Color.LightGray, // Неактивный цвет
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // 2. Кнопка "Группировка"
            item {
                DnsFilterChip(text = "Группировка")
            }

            // 3. Кнопка "Поиск по категории"
            item {
                DnsFilterChip(text = "Поиск по категории")
            }
        }
    }
}

// Вспомогательный компонент для кнопок-чипсов (серых овалов)
@Composable
fun DnsFilterChip(
    text: String,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        color = Color(0xFFF5F5F5), // Цвет фона (#F5F5F5 - стандартный светло-серый)
        shape = CircleShape,       // Скругленные края
        modifier = Modifier.height(32.dp) // Фиксированная высота как на скрине
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Black,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}
