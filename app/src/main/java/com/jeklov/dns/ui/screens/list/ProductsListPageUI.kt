package com.jeklov.dns.ui.screens.list

import android.app.Application
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jeklov.dns.data.api.product.model.ProductViewModelProviderFactory
import com.jeklov.dns.data.api.product.models.ProductListRequest
import com.jeklov.dns.data.api.product.repository.ProductRepository
import com.jeklov.dns.data.api.productList.model.ProductViewModel
import com.jeklov.dns.data.util.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// Константа цвета, используемая для индикатора загрузки
private val DnsOrangeMain = Color(0xFFFC8507)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListPageUI(
    paddingValues: PaddingValues,
    navigationController: NavHostController,
    application: Application,
) {
    val category = "Смартфоны"

    val viewModel: ProductViewModel = viewModel(
        factory = ProductViewModelProviderFactory(
            application,
            ProductRepository()
        )
    )
    val productList by viewModel.productListToken.collectAsStateWithLifecycle()

    // State для логики скрытия хедера (как в MainPageUI)
    var topRowHeightPx by remember { mutableIntStateOf(0) }
    var stickyHeaderHeightPx by remember { mutableIntStateOf(0) }
    val headerOffset = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    // Pull to refresh state
    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        if (productList == null) {
            viewModel.productList(ProductListRequest(category_name = category))
        }
    }

    // Логика NestedScroll (Quick Return)
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val currentOffset = headerOffset.value
                // Скрываем только верхнюю часть (topRowHeightPx), нижняя (stickyHeaderHeightPx) остается
                val newOffset = (currentOffset + delta).coerceIn(-topRowHeightPx.toFloat(), 0f)

                coroutineScope.launch {
                    headerOffset.snapTo(newOffset)
                }
                return Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
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
                .clipToBounds()
                .nestedScroll(nestedScrollConnection)
        ) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    coroutineScope.launch {
                        isRefreshing = true
                        // Здесь можно добавить логику обновления списка через ViewModel
                        viewModel.productList(ProductListRequest(category_name = category))
                        delay(1000)
                        isRefreshing = false
                    }
                },
                state = pullToRefreshState,
                modifier = Modifier.fillMaxSize(),
                indicator = {
                    val indicatorTopPadding =
                        with(density) { (stickyHeaderHeightPx).toDp() }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = indicatorTopPadding)
                    ) {
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
                        PullToRefreshDefaults.Indicator(
                            state = pullToRefreshState,
                            isRefreshing = isRefreshing,
                            containerColor = Color.White,
                            color = DnsOrangeMain,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            ) {
                // Контент списка
                val contentTopPadding =
                    with(density) { (topRowHeightPx + stickyHeaderHeightPx).toDp() }

                when (val state = productList) {
                    is Resource.Success<*> -> {
                        val userData = (productList as Resource.Success).data
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(top = contentTopPadding, bottom = 16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                FilterHeader()
                            }
                            items(userData.size) { index ->
                                val product = userData[index]
                                Box(modifier = Modifier.padding(horizontal = 22.dp)) {
                                    ProductItem(
                                        title = product.model,
                                        imageUrl = product.src,
                                        price = product.price,
                                        rating = 4.8,
                                        reviewsCount = 128
                                    )
                                }
                            }
                        }
                    }

                    is Resource.Error -> {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(top = contentTopPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Ошибка загрузки", color = Color.Red)
                        }
                    }

                    is Resource.Loading -> {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(top = contentTopPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = DnsOrangeMain)
                        }
                    }

                    null -> {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(top = contentTopPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Инициализация...")
                        }
                    }
                }
            }

            // Плавающий хедер
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .offset { IntOffset(x = 0, y = headerOffset.value.roundToInt()) }
            ) {
                // 1. Верхняя часть (которая скрывается при скролле).
                // В ProductListPage пока нет CityRow, но слот под него готов.
                // Если topRowHeightPx == 0, то скроллиться ничего не будет, хедер будет просто прилипшим.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White) // Фон важен, чтобы не просвечивал контент
                        .onGloballyPositioned { coordinates ->
                            if (topRowHeightPx == 0) {
                                topRowHeightPx = coordinates.size.height
                            }
                        }
                ) {
                    // Сюда можно добавить TopCityAndDiscountsRow() если понадобится
                }

                // 2. Нижняя часть (которая "прилипает" и остается видимой).
                // Используем ProductListTopBar здесь.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .onGloballyPositioned { coordinates ->
                            if (stickyHeaderHeightPx == 0) {
                                stickyHeaderHeightPx = coordinates.size.height
                            }
                        }
                ) {
                    ProductListTopBar(
                        title = category,
                        onBackClick = { navigationController.popBackStack() },
                        onSearchClick = { /* Обработка поиска */ }
                    )
                }
            }
        }
    }
}
