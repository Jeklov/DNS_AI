package com.jeklov.dns.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jeklov.dns.R

data class CategoryItem(
    val title: String,
    val imageRes: Int
)

@Composable
fun CategoriesSection() {
    // Создаем список объектов с текстом и картинкой
    // ВАЖНО: Замените R.drawable.img_... на названия ваших реальных .webp файлов в папке res/drawable
    val categories = listOf(
        CategoryItem("Бытовая\nтехника", R.drawable.ic_catalog_appliances), // Пример
        CategoryItem("Красота и\nздоровье", R.drawable.ic_catalog_health),
        CategoryItem("Смартфоны и\nфототехника", R.drawable.ic_catalog_phones),
        CategoryItem("ТВ, консоли\nи аудио", R.drawable.ic_catalog_tv),
        CategoryItem("ПК, ноутбуки\nпериферия", R.drawable.ic_catalog_notebooks),
        CategoryItem("Комплект\nующие для ПК", R.drawable.ic_catalog_pc_notebooks)
    )

    LazyRow(
        contentPadding = PaddingValues(22.dp),
        horizontalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        items(categories) { category ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .height(100.dp)
                    .clip(RoundedCornerShape(10.dp)) // 1. Обрезаем Column по форме
                    .clickable { /* Навигация в категорию */ } // 2. Применяем клик после обрезки
                    .background(Color.Transparent) // 3. Устанавливаем фон
            ) {
                // Контейнер для картинки
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Отображаем картинку из модели
                    Image(
                        painter = painterResource(id = category.imageRes),
                        contentDescription = category.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                // Текст из модели
                Text(
                    text = category.title,
                    fontSize = 13.sp,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
