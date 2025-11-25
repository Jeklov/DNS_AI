package com.jeklov.dns.ui.screens.main

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jeklov.dns.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class ProductUIModel(
    val id: Int,
    val title: String,
    val price: String,
    val monthlyPayment: String,
    val rating: Double,
    val reviewsCount: Int,
    val imageRes: Int,
    val badgeIconRes: Int? = null,
    val badgeColor: Color? = null
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPageUI(
    paddingValues: PaddingValues,
    navigationController: NavHostController,
) {
    var topRowHeightPx by remember { mutableIntStateOf(0) }
    var searchRowHeightPx by remember { mutableIntStateOf(0) }

    // Используем Animatable для плавного смещения и доводки
    val headerOffset = remember { Animatable(0f) }

    val coroutineScope = rememberCoroutineScope()

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val currentOffset = headerOffset.value

                // ИЗМЕНЕНИЕ ЗДЕСЬ:
                // Мы убрали проверку "if (delta < 0 || !isFullyCollapsed)".
                // Теперь шапка меняет положение при ЛЮБОМ скролле.
                // Тянем вниз (delta > 0) -> шапка выезжает (Quick Return).
                // Тянем вверх (delta < 0) -> шапка прячется.

                val newOffset = (currentOffset + delta).coerceIn(-topRowHeightPx.toFloat(), 0f)

                coroutineScope.launch {
                    headerOffset.snapTo(newOffset)
                }

                // Возвращаем Zero, чтобы LazyColumn тоже получал этот скролл и двигался
                // одновременно с шапкой.
                return Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                // Логика доводчика (snap)
                // Если шапка скрыта более чем наполовину -> дозакрываем, иначе открываем.
                val isClosing = headerOffset.value < -topRowHeightPx / 2f
                val targetOffset = if (isClosing) -topRowHeightPx.toFloat() else 0f

                coroutineScope.launch {
                    headerOffset.animateTo(
                        targetValue = targetOffset,
                        animationSpec = tween(durationMillis = 300)
                    )
                }
                return Velocity.Zero
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(bottom = paddingValues.calculateBottomPadding())
            .padding(top = paddingValues.calculateTopPadding())
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds() // Обрезаем шапку, когда она уезжает вверх
                .nestedScroll(nestedScrollConnection)
        ) {
            val listState = rememberLazyListState()
            var isRefreshing by remember { mutableStateOf(false) }
            val pullToRefreshState = rememberPullToRefreshState()
            val density = LocalDensity.current

            val products = remember {
                listOf(
                    ProductUIModel(
                        id = 1,
                        title = "17.3\" Ноутбук ARDOR GAMING NEO N17-I5ND403 черный",
                        price = "57 999 ₽",
                        monthlyPayment = "от 2 462 ₽/ мес.",
                        rating = 4.8,
                        reviewsCount = 346,
                        imageRes = R.drawable.ic_home_rsu,
                        badgeIconRes = android.R.drawable.ic_input_add,
                        badgeColor = Color(0xFFFF6000)
                    ),
                    ProductUIModel(
                        id = 2,
                        title = "Фен Sheo Aurora серебристый",
                        price = "3 699 ₽",
                        monthlyPayment = "от 157 ₽/ мес.",
                        rating = 4.5,
                        reviewsCount = 190,
                        imageRes = R.drawable.ic_home_rsu,
                        badgeIconRes = android.R.drawable.star_on,
                        badgeColor = Color(0xFF6200EE)
                    ),
                    ProductUIModel(
                        id = 3,
                        title = "Смартфон Apple iPhone 15 128 ГБ черный",
                        price = "84 999 ₽",
                        monthlyPayment = "от 3 600 ₽/ мес.",
                        rating = 4.9,
                        reviewsCount = 1205,
                        imageRes = R.drawable.ic_home_rsu,
                        badgeIconRes = null,
                        badgeColor = null
                    ),
                    ProductUIModel(
                        id = 4,
                        title = "Наушники TWS Samsung Galaxy Buds2 Pro фиолетовый",
                        price = "14 499 ₽",
                        monthlyPayment = "от 615 ₽/ мес.",
                        rating = 4.7,
                        reviewsCount = 540,
                        imageRes = R.drawable.ic_home_rsu
                    ),
                )
            }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    coroutineScope.launch {
                        isRefreshing = true
                        delay(1000)
                        isRefreshing = false
                    }
                },
                state = pullToRefreshState,
                modifier = Modifier.fillMaxSize(),
                indicator = {
                    val indicatorTopPadding = with(density) { (searchRowHeightPx).toDp() } + 10.dp
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = indicatorTopPadding)
                    ) {
                        PullToRefreshDefaults.Indicator(
                            state = pullToRefreshState,
                            isRefreshing = isRefreshing,
                            containerColor = Color.White,
                            color = Color(0xFFFF6000),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(
                        top = with(density) { (topRowHeightPx + searchRowHeightPx).toDp() },
                        bottom = 16.dp
                    )
                ) {
                    item { CategoriesSection() }
                    item { QuickActionsSection() }
                    item {
                        Text(
                            text = "Вам может понравиться",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 12.dp)
                        )
                    }
                    items(products.chunked(2)) { rowProducts ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (product in rowProducts) {
                                Box(modifier = Modifier.weight(1f)) {
                                    ProductCardExact(product = product)
                                }
                            }
                            if (rowProducts.size == 1) {
                                Box(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .offset { IntOffset(x = 0, y = headerOffset.value.roundToInt()) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .onGloballyPositioned { coordinates ->
                            if (topRowHeightPx == 0) {
                                topRowHeightPx = coordinates.size.height
                            }
                        }
                ) {
                    TopCityAndDiscountsRow()
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            if (searchRowHeightPx == 0) {
                                searchRowHeightPx = coordinates.size.height
                            }
                        }
                ) {
                    SearchBarHeader(navigationController)
                }
            }
        }
    }
}
