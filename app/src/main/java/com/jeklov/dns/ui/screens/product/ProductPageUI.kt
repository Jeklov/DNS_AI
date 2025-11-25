@file:Suppress("DEPRECATION")

package com.jeklov.dns.ui.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage // Requires io.coil-kt:coil-compose dependency
import com.jeklov.dns.R

// Helper data class to normalize the diverse input JSONs
data class ProductUiState(
    val imageUrl: String,
    val title: String,
    val price: Int?,
    val oldPrice: Int? = null,
    val description: String = "", // For "Description" tab
    val specs: List<Pair<String, String>> // For "Characteristics" tab
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductPageUI(
    paddingValues: PaddingValues,
    navigationController: NavHostController,
    rawProductData: Map<String, Any>? // Passed from your ViewModel/Screen loader
) {
    // 1. Parse the dynamic data into a stable UI state
    /*val productState = remember(rawProductData) { parseProductData(rawProductData) }

    // Tabs setup
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Описание", "Характеристики", "Отзывы", "Обзоры")

    Scaffold(
        modifier = Modifier
            .padding(bottom = paddingValues.calculateBottomPadding())
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navigationController.popBackStack() }) {
                        Icon(painter = painterResource(R.drawable.ic_profile_quit), contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(painter = painterResource(R.drawable.ic_profile_feedback), contentDescription = "Share")
                    }
                }
            )
        },
        bottomBar = {
            BottomPriceBar(productState)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            // --- Image Carousel Section ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Using AsyncImage from Coil for network images
                    AsyncImage(
                        model = productState.imageUrl,
                        contentDescription = productState.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )

                    // Overlay buttons (Like, Compare) could go here
                }
            }

            // --- Title and Rating ---
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = productState.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Mock Rating Row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("★★★★☆", color = Color(0xFFFFA000))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("128", color = Color.Gray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("О товаре", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            // --- Tabs ---
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 16.dp,
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Color(0xFFFFA000) // DNS Orange-ish
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    title,
                                    color = if (selectedTabIndex == index) Color.Black else Color.Gray
                                )
                            }
                        )
                    }
                }
            }

            // --- Tab Content ---
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (selectedTabIndex == 1) { // Characteristics Tab
                // We group common technical specs
                item {
                    Text(
                        "Заводские данные",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                }

                items(productState.specs) { (key, value) ->
                    SpecRow(key, value)
                }
            } else {
                // Placeholder for Description/Reviews
                item {
                    Text(
                        text = "Информация раздела '${tabs[selectedTabIndex]}' временно недоступна",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // Space for bottom bar
            }
        }
    }*/
}

@Composable
fun SpecRow(key: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = key,
            modifier = Modifier.weight(1f),
            color = Color.Gray,
            fontSize = 14.sp
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray)
}

@Composable
fun BottomPriceBar(productState: ProductUiState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 16.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                if (productState.price != null) {
                    Text(
                        text = "${productState.price} ₽",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    if (productState.oldPrice != null) {
                        Text(
                            text = "${productState.oldPrice} ₽",
                            fontSize = 12.sp,
                            textDecoration = TextDecoration.LineThrough,
                            color = Color.Gray
                        )
                    }
                } else {
                    Text("Нет в наличии", color = Color.Gray)
                }
            }

            Button(
                onClick = { /* Add to cart */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000)), // Orange
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text("Купить", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- Logic to convert loose JSON Map to UI State ---
fun parseProductData(data: Map<String, Any>): ProductUiState {
    // 1. Extract known common fields
    val image = (data["Изображение"] as? String)
        ?: (data["image"] as? String)
        ?: ""

    val manufacturer = (data["Производитель"] as? String) ?: (data["brand"] as? String) ?: ""
    val model = (data["Модель"] as? String) ?: ""

    // Construct a display title
    val rawName = data["name"] as? String
    val title = rawName ?: "$manufacturer $model".trim()

    // Price extraction (handle both Int and String inputs)
    val priceAny = data["Цена"] ?: data["salePrice"]
    val price = priceAny?.toString()?.toIntOrNull()

    val oldPriceAny = data["Цена без скидки"] ?: data["basePrice"]
    val oldPrice = oldPriceAny?.toString()?.toIntOrNull()

    // 2. Extract everything else as Specifications
    val excludeKeys = setOf(
        "Изображение", "image", "link", "productId", "salePrice", "basePrice",
        "bonus", "Цена", "Цена без скидки", "Скидка", "name", "properties"
    )

    val specs = mutableListOf<Pair<String, String>>()

    // Handle nested "properties" object (like in the Huawei example)
    val nestedProps = data["properties"] as? Map<*, *>
    if (nestedProps != null) {
        nestedProps.forEach { (k, v) ->
            specs.add(k.toString() to v.toString())
        }
    } else {
        // Handle flat structure (like SSD, GPU examples)
        data.forEach { (key, value) ->
            if (key !in excludeKeys) {
                // Handle arrays (like "Поддержка технологий")
                val displayValue = if (value is List<*>) {
                    value.joinToString(", ")
                } else {
                    value.toString()
                }
                specs.add(key to displayValue)
            }
        }
    }

    return ProductUiState(
        imageUrl = image,
        title = title,
        price = price,
        oldPrice = oldPrice,
        specs = specs
    )
}
