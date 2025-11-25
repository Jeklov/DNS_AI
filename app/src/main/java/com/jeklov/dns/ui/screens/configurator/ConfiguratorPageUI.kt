package com.jeklov.dns.ui.screens.configurator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.jeklov.dns.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguratorPageUI(
    paddingValues: PaddingValues,
    navigationController: NavHostController,
    //database: MainDB,
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = paddingValues.calculateBottomPadding()), // Учитываем нижний бар навигации
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Конфигуратор ПК",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        textAlign = TextAlign.Center,
                        color = Color.Black,

                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .clickable { /* TODO: Логика добавления */ },
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Добавьте комплектующую",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                    )
                }
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {

            // Блок 2: Инструкция
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        InfoRow(
                            title = "Выберите комплектующие",
                            subtitle = "Начинайте с любой категории",
                            icon = painterResource(R.drawable.ic_home_configurator) // Замените на иконку процессора (цветную)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        InfoRow(
                            title = "Соберите полный комплект",
                            subtitle = "Завершив сборку, вы можете добавить её в корзину или сохранить в личном кабинете",
                            icon = painterResource(R.drawable.ic_home_rsu) // Замените на иконку корпуса (цветную)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider1px()
                        ActionRow(
                            icon = null, // Здесь нет левой иконки
                            text = "Справка по конфигуратору",
                            showArrow = true
                        )
                    }
                }
            }

            // Блок 2: Действия (ИИ, Собрать с нуля, Улучшить)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        ActionRow(
                            icon = painterResource(R.drawable.ic_profile_offers), // Замените на иконку "ИИ" (мозг/чип)
                            text = "ИИ ассистент поможет с сборкой!",
                            showArrow = false,
                            clickable = false
                        )
                        Divider1px()
                        ActionRow(
                            icon = painterResource(R.drawable.ic_profile_offers), // Замените на иконку "Процессор/сокет"
                            text = "Собрать с нуля",
                            showArrow = true
                        )
                        Divider1px()
                        ActionRow(
                            icon = painterResource(R.drawable.ic_profile_offers), // Замените на иконку "Флаг/Инструменты"
                            text = "Улучшить компьютер",
                            showArrow = true
                        )
                    }
                }
            }

            // Блок 3: Обязательные комплектующие
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Обязательные комплектующие",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "0/7",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    painter = painterResource(R.drawable.ic_profile_about), // Иконка знака вопроса в круге
                                    contentDescription = "Info",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Прогресс бар (серый фон)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .height(8.dp)
                                .background(Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Divider1px()
                        ActionRow(
                            icon = painterResource(R.drawable.ic_profile_offers),
                            text = "Процессор",
                            showArrow = true
                        )
                        Divider1px()
                        ActionRow(
                            icon = painterResource(R.drawable.ic_profile_offers),
                            text = "Материнская плата",
                            showArrow = true
                        )
                        Divider1px()
                        ActionRow(
                            icon = painterResource(R.drawable.ic_profile_offers),
                            text = "Блок питания",
                            showArrow = true
                        )
                        Divider1px()
                        ActionRow(
                            icon = painterResource(R.drawable.ic_profile_offers),
                            text = "Корпус",
                            showArrow = true
                        )
                        Divider1px()
                        ActionRow(
                            icon = painterResource(R.drawable.ic_profile_offers),
                            text = "Видеокарта",
                            showArrow = true
                        )
                        Divider1px()
                        ActionRow(
                            icon = painterResource(R.drawable.ic_profile_offers),
                            text = "Охлаждение процессора",
                            showArrow = true
                        )
                        Divider1px()
                        ActionRow(
                            icon = painterResource(R.drawable.ic_profile_offers),
                            text = "Оперативная память",
                            showArrow = true
                        )
                        Divider1px()
                        ActionRow(
                            icon = painterResource(R.drawable.ic_profile_offers), // Иконка платы
                            text = "Хранение данных",
                            showArrow = true
                        )
                    }
                }
            }

            // --- БЛОК 1: Дополнительные детали ---
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        SectionHeader("Дополнительные детали")
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp), // Уменьшил отступ между элементами
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            item { ComponentItem("Привод", painterResource(R.drawable.ic_profile_offers)) }
                            item { ComponentItem("Радиаторы M.2", painterResource(R.drawable.ic_profile_offers)) }
                            item { ComponentItem("Доп. вентиляторы", painterResource(R.drawable.ic_profile_offers)) }
                            item { ComponentItem("Термоинтерфейс", painterResource(R.drawable.ic_profile_offers)) }
                        }
                    }
                }
            }

            // --- БЛОК 2: Периферия ---
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        SectionHeader("Периферия")
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            item { ComponentItem("Монитор", painterResource(R.drawable.ic_profile_offers)) }
                            item { ComponentItem("Сетевой фильтр", painterResource(R.drawable.ic_profile_offers)) }
                            item { ComponentItem("Клавиатура", painterResource(R.drawable.ic_profile_offers)) }
                            item { ComponentItem("Мышь", painterResource(R.drawable.ic_profile_offers)) }
                            item { ComponentItem("Аудиоколонки", painterResource(R.drawable.ic_profile_offers)) }
                            item { ComponentItem("Клавиатура+мышь", painterResource(R.drawable.ic_profile_offers)) }
                            item { ComponentItem("Адаптер Wi-Fi", painterResource(R.drawable.ic_profile_offers)) }
                            item { ComponentItem("ИБП", painterResource(R.drawable.ic_profile_offers)) }
                        }
                    }
                }
            }

            // --- БЛОК 3: Программное обеспечение ---
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)) {
                        SectionHeader("Программное обеспечение")
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            item { ComponentItem("ОС", painterResource(R.drawable.ic_profile_offers)) }
                            item { ComponentItem("Антивирус", painterResource(R.drawable.ic_profile_offers)) }
                            item { ComponentItem("Офисный пакет", painterResource(R.drawable.ic_profile_offers)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun ComponentItem(text: String, painter: Painter) {
    // Карточка элемента: минимальная высота, компактная ширина
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        modifier = Modifier
            .clip(shape = RoundedCornerShape(12.dp))
            .clickable { }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Центрирование содержимого
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp) // Чуть меньше иконка
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 11.sp
            )
        }
    }
}

@Composable
fun ComponentChip(text: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        modifier = Modifier.clickable { }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // TODO: Add Icon here if needed
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )
        }
    }
}


@Composable
fun ActionRow(
    icon: Painter?,
    text: String,
    showArrow: Boolean,
    padding: PaddingValues = PaddingValues(16.dp),
    clickable: Boolean = true,
) {
    val MyModifier = if (!clickable) Modifier else Modifier.clickable {}

    Row(
        modifier = MyModifier
            .fillMaxWidth()
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
        }

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.DarkGray,
            modifier = Modifier.weight(1f)
        )

        if (showArrow) {
            Icon(
                painter = painterResource(R.drawable.ic_profile_quit),
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}


@Composable
fun InfoRow(
    title: String,
    subtitle: String,
    icon: Painter
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                lineHeight = 18.sp
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            painter = icon,
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(64.dp)
        )

    }
}

@Composable
fun Divider1px() {
    HorizontalDivider(
        thickness = 1.dp,
        color = Color(0xFFF0F0F0)
    )
}
