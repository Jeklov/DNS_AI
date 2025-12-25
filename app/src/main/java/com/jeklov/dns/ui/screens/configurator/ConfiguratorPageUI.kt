package com.jeklov.dns.ui.screens.configurator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.jeklov.dns.MainActivity
import com.jeklov.dns.R
import com.jeklov.dns.data.api.ai.chat.models.AIMode
import com.jeklov.dns.data.api.ai.chat.models.AIProductObject
import com.jeklov.dns.data.user.SharedPreference
import com.jeklov.dns.ui.screens.Screens
import com.jeklov.dns.ui.screens.list.ProductItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguratorPageUI(
    paddingValues: PaddingValues,
    navigationController: NavHostController,
    contextM: MainActivity,
    //database: MainDB,
) {
    // 1. Load configuration data
    val configuration = remember { SharedPreference.LoadData(contextM).getAIChatResponse() }

    // 2. Helper map to find components by category easily
    // Note: Assumes 'components' is a List in your response object.
    // We convert the list to a Map<CategoryString, ComponentObject> for quick access.
    val componentsByCategory = remember(configuration) {
        configuration?.components?.associateBy { it.category } ?: emptyMap()
    }

    // 3. Define required categories to track progress
    val requiredCategories = listOf(
        "cpus", "motherboards", "power_supplies", "cases",
        "gpus", "fans", "ram", "ssds" // Added ssds as storage
    )

    // Calculate progress
    val filledCount = requiredCategories.count { componentsByCategory.containsKey(it) }
    val totalCount = requiredCategories.size


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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (configuration?.price != null && configuration.price > 0)
                                "Итого: ${configuration.price} ₽"
                            else "Добавьте комплектующую",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333)
                            )
                        )
                        if (configuration?.price != null && configuration.price > 0) {
                            Text(
                                text = "Купить или сохранить",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                            )
                        }
                    }
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

            // Блок 1: Инструкция
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
                            icon = painterResource(R.drawable.ic_home_configurator),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        InfoRow(
                            title = "Соберите полный комплект",
                            subtitle = "Завершив сборку, вы можете добавить её в корзину или сохранить в личном кабинете",
                            icon = painterResource(R.drawable.ic_home_rsu)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider1px()
                        ActionRow(
                            icon = null,
                            text = "Справка по конфигуратору",
                            showArrow = true,
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
                            icon = painterResource(R.drawable.ic_ai_assistant),
                            text = "ИИ ассистент поможет с сборкой!",
                            showArrow = false,
                            size = 6.dp,
                        )
                        Divider1px()
                        ActionRow(
                            icon = painterResource(R.drawable.ic_rsu_new),
                            text = "Собрать с нуля",
                            showArrow = true,
                            size = 6.dp,
                            modifier = Modifier.clickable {
                                navigationController.navigate(Screens.AssistantPage.screen + "/" + "/" + AIMode.Configuration.name)
                            }                        )
                        Divider1px()
                        ActionRow(
                            icon = painterResource(R.drawable.ic_rsu_improve),
                            text = "Улучшить компьютер",
                            showArrow = true,
                            size = 6.dp,
                            modifier = Modifier.clickable {
                                navigationController.navigate(Screens.AssistantPage.screen + "/" + "/" + AIMode.Configuration.name)
                            }
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
                                    text = "$filledCount/$totalCount",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    painter = painterResource(R.drawable.ic_profile_about),
                                    contentDescription = "Info",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Прогресс бар
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFEEEEEE))
                        ) {
                            // Filled part of progress bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = if (totalCount > 0) filledCount.toFloat() / totalCount else 0f)
                                    .height(8.dp)
                                    .background(Color(0xFFFF8C00), RoundedCornerShape(8.dp))
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Dynamic Rows based on Category

                        Divider1px()
                        ConfiguratorComponentRow(
                            defaultIcon = painterResource(R.drawable.ic_rsu_cpu),
                            defaultTitle = "Процессор",
                            componentData = componentsByCategory["cpus"]
                        )

                        Divider1px()
                        ConfiguratorComponentRow(
                            defaultIcon = painterResource(R.drawable.ic_rsu_motherboard),
                            defaultTitle = "Материнская плата",
                            componentData = componentsByCategory["motherboards"]
                        )

                        Divider1px()
                        ConfiguratorComponentRow(
                            defaultIcon = painterResource(R.drawable.ic_rsu_power_unit),
                            defaultTitle = "Блок питания",
                            componentData = componentsByCategory["bps"] ?: componentsByCategory["power_supplies"]
                        )

                        Divider1px()
                        ConfiguratorComponentRow(
                            defaultIcon = painterResource(R.drawable.ic_rsu_case),
                            defaultTitle = "Корпус",
                            componentData = componentsByCategory["cases"]
                        )

                        Divider1px()
                        ConfiguratorComponentRow(
                            defaultIcon = painterResource(R.drawable.ic_rsu_gpu),
                            defaultTitle = "Видеокарта",
                            componentData = componentsByCategory["gpus"]
                        )

                        Divider1px()
                        ConfiguratorComponentRow(
                            defaultIcon = painterResource(R.drawable.ic_rsu_cold),
                            defaultTitle = "Охлаждение процессора",
                            componentData = componentsByCategory["fans"]
                        )

                        Divider1px()
                        ConfiguratorComponentRow(
                            defaultIcon = painterResource(R.drawable.ic_rsu_ram),
                            defaultTitle = "Оперативная память",
                            componentData = componentsByCategory["ram"]
                        )

                        Divider1px()
                        ConfiguratorComponentRow(
                            defaultIcon = painterResource(R.drawable.ic_rsu_hdd_ssd),
                            defaultTitle = "Хранение данных",
                            componentData = componentsByCategory["ssds"] ?: componentsByCategory["hdds"]
                        )

                        Divider1px()
                        // Optional or not present in JSON example categories
                        ConfiguratorComponentRow(
                            defaultIcon = painterResource(R.drawable.ic_rsu_periphery),
                            defaultTitle = "Периферия",
                            componentData = componentsByCategory["periphery"]
                        )

                        Divider1px()
                        ConfiguratorComponentRow(
                            defaultIcon = painterResource(R.drawable.ic_rsu_soft),
                            defaultTitle = "Программное обеспечение",
                            componentData = componentsByCategory["software"]
                        )
                    }
                }
            }
        }
    }
}

/**
 * A smart row that switches between "Add new" state and "Show selected item" state
 */
@Composable
fun ConfiguratorComponentRow(
    defaultIcon: Painter,
    defaultTitle: String,
    componentData: AIProductObject? = null
) {
    if (componentData != null) {
        // FILLED STATE: Используем ProductItem
        ProductItem(
            title = componentData.model ?: defaultTitle,
            imageUrl = componentData.src,
            price = componentData.price ?: 0,
            oldPrice = ((componentData.price ?: 0) * 1.2).toInt(),
            rating = 4.8,
            reviewsCount = 123,
            onClick = { /* Обработка клика по товару */ }
        )
    } else {
        // EMPTY STATE: Стандартная строка для выбора
        ActionRow(
            icon = defaultIcon,
            text = defaultTitle,
            showArrow = true
        )
    }
}


@Composable
fun ActionRow(
    icon: Painter?,
    text: String,
    showArrow: Boolean,
    padding: PaddingValues = PaddingValues(16.dp),
    size: Dp = 0.dp,
    modifier: Modifier = Modifier
) {
    val MyModifier = modifier

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
                modifier = Modifier
                    .size(40.dp)
                    .padding(size)
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
                painter = painterResource(R.drawable.ic_arrow_path),
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(20.dp)
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
