package com.jeklov.dns.ui.screens.assistant

import android.app.Application
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jeklov.dns.MainActivity
import com.jeklov.dns.R
import com.jeklov.dns.data.api.ai.aiChat.model.AIChatViewModel
import com.jeklov.dns.data.api.ai.chat.model.AIChatViewModelProviderFactory
import com.jeklov.dns.data.api.ai.chat.models.AIChatRequest
import com.jeklov.dns.data.api.ai.chat.models.AIMode
import com.jeklov.dns.data.api.ai.chat.models.AIProductObject
import com.jeklov.dns.data.api.ai.chat.repository.AIChatRepository
import com.jeklov.dns.data.api.chat.models.ChatRequest
import com.jeklov.dns.data.api.chat.repository.ChatRepository
import com.jeklov.dns.data.api.chat.view.model.ChatViewModel
import com.jeklov.dns.data.api.chat.view.model.ChatViewModelProviderFactory
import com.jeklov.dns.data.user.SharedPreference
import com.jeklov.dns.data.util.Resource
import com.jeklov.dns.ui.screens.Screens
import com.jeklov.dns.ui.screens.list.ProductItem
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography

// --- МОДЕЛИ ---
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isError: Boolean = false,
    // Добавляем список компонентов (предполагаю, что AIComponentModel - это тип элементов в data.components)
    val products: List<AIProductObject>? = null,
)

@Composable
fun AssistantPageUI(
    paddingValues: PaddingValues,
    navigationController: NavHostController,
    application: Application,
    prompt: String?,
    mode: AIMode = AIMode.Text,
    contextM: MainActivity,
) {
    // 1. Инициализация ViewModels
    // ViewModel для обычного чата
    val viewModel: ChatViewModel = viewModel(
        factory = ChatViewModelProviderFactory(application, ChatRepository())
    )

    // ViewModel для AI конфигуратора (и других режимов)
    val aiViewModel: AIChatViewModel = viewModel(
        factory = AIChatViewModelProviderFactory(application, AIChatRepository())
    )

    // 2. Состояния
    val chatState by viewModel.chatToken.collectAsStateWithLifecycle()
    val aiChatState by aiViewModel.aiChatToken.collectAsStateWithLifecycle()

    val messages = remember { mutableStateListOf<ChatMessage>() }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    // Состояние текущего режима
    var currentMode by remember { mutableStateOf(mode) }

    // 3. Обработка ответов от сервера (Text Mode)
    LaunchedEffect(chatState) {
        if (currentMode == AIMode.Text) {
            when (val state = chatState) {
                is Resource.Success -> {
                    val responseText = state.data.response
                    if (messages.lastOrNull()?.text != responseText) {
                        messages.add(ChatMessage(responseText, isUser = false))
                    }
                }

                is Resource.Error -> {
                    val errorMsg = state.exception
                    messages.add(ChatMessage(errorMsg, isUser = false, isError = true))
                    Toast.makeText(context, "Ошибка: $errorMsg", Toast.LENGTH_SHORT).show()
                }

                else -> { /* Loading handled by UI */
                }
            }
        }
    }

    // 3.1 Обработка ответов от сервера (AI Configuration Mode)
    LaunchedEffect(aiChatState) {
        if (currentMode != AIMode.Text) {
            when (val state = aiChatState) {
                is Resource.Success -> {
                    val data = state.data

                    // --- ДОБАВЛЕНО СОХРАНЕНИЕ ---
                    // Сохраняем полученный ответ конфигуратора
                    SharedPreference.SaveData(context).saveAIChatResponse(data)
                    // -----------------------------

                    val formattedResponse = buildString {
                        append(data.comment)
                    }

                    val lastMsg = messages.lastOrNull()
                    // Проверка на дубликаты
                    if (lastMsg == null || lastMsg.isUser || lastMsg.text != formattedResponse) {
                        messages.add(
                            ChatMessage(
                                text = formattedResponse,
                                isUser = false,
                                // Передаем компоненты в сообщение
                                products = data.components
                            )
                        )
                    }
                }

                is Resource.Error -> {
                    val errorMsg = state.exception
                    messages.add(ChatMessage("Ошибка: $errorMsg", isUser = false, isError = true))
                    Toast.makeText(context, "Ошибка: $errorMsg", Toast.LENGTH_SHORT).show()
                }

                else -> { /* Loading handled by UI */
                }
            }
        }
    }

    // 4. Автопрокрутка вниз при добавлении сообщений
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // 5. Функция отправки
    fun sendMessage(text: String) {
        if (text.isBlank()) return

        // Добавляем в UI только текст пользователя
        messages.add(ChatMessage(text, true))

        if (currentMode == AIMode.Text) {
            // Старая логика для текстового режима
            viewModel.chat(ChatRequest(text))
        } else {
            // Новая логика для конфигуратора
            aiViewModel.aiChat(
                AIChatRequest(
                    prompt = text,
                    mode = currentMode
                )
            )
        }
    }

    // --- 6. Обработка входящего prompt (автоотправка) ---
    var hasSentPrompt by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!prompt.isNullOrBlank() && !hasSentPrompt) {
            sendMessage(prompt)
            hasSentPrompt = true
        }
    }

    val handleBack: () -> Unit = {
        val targetRoute = navigationController.previousBackStackEntry?.destination?.route
        if (targetRoute == Screens.SearchPage.screen) {
            navigationController.navigate(Screens.MainPage.screen) { popUpTo(0) }
        } else {
            try {
                navigationController.popBackStack()
            } catch (e: Exception) {
                navigationController.navigate(Screens.MainPage.screen) { popUpTo(0) }
            }
        }
    }

    BackHandler(enabled = true, onBack = handleBack)

    // Вычисляем общее состояние загрузки
    val isAnyLoading = chatState is Resource.Loading || aiChatState is Resource.Loading

    // 7. Отображение экрана
    AssistantScreen(
        paddingValues = paddingValues,
        messages = messages,
        isLoading = isAnyLoading,
        listState = listState,
        currentMode = currentMode,
        onModeChange = { newMode -> currentMode = newMode },
        onBackClick = handleBack,
        onHistoryClick = { navigationController.navigate(Screens.AssistantHistoryPage.screen) },
        onSendMessage = { sendMessage(it) },
        context = contextM
    )
}


// --- ОСНОВНОЙ ЭКРАН (UI) ---
@Composable
fun AssistantScreen(
    paddingValues: PaddingValues,
    messages: List<ChatMessage>,
    isLoading: Boolean,
    listState: LazyListState,
    currentMode: AIMode,
    onModeChange: (AIMode) -> Unit,
    onBackClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSendMessage: (String) -> Unit,
    context: MainActivity,
) {
    val density = LocalDensity.current
    val keyboardHeightPx = WindowInsets.ime.getBottom(density)
    val keyboardHeightDp = with(density) { keyboardHeightPx.toDp() }

    Box(
        Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding(), bottom = keyboardHeightDp)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.White,
            contentWindowInsets = WindowInsets.ime,
            topBar = {
                AssistantTopBar(onBackClick, onHistoryClick)
            },
            bottomBar = {
                AssistantInputArea(
                    isLoading = isLoading,
                    currentMode = currentMode,
                    onModeChange = onModeChange,
                    onSendMessage = onSendMessage
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (messages.isEmpty()) {
                    EmptyChatPlaceholder(modifier = Modifier.align(Alignment.Center))
                } else {
                    MessageList(
                        messages = messages,
                        isLoading = isLoading,
                        listState = listState,
                        context = context,
                    )
                }
            }
        }
    }
}

// --- КОМПОНЕНТЫ ---

@Composable
fun AssistantTopBar(
    onBackClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back_black_24),
                contentDescription = "Назад",
                tint = Color.Gray,
                modifier = Modifier
                    .size(36.dp)
                    .padding(2.dp),
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.ic_ai_assistant),
                contentDescription = "AI",
                tint = Color.Black,
                modifier = Modifier.size(25.dp),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "ИИ Ассистент",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        IconButton(onClick = onHistoryClick) {
            Icon(
                painter = painterResource(R.drawable.ic_ai_folder_open),
                contentDescription = "История",
                tint = Color.Gray,
                modifier = Modifier
                    .size(36.dp)
                    .padding(2.dp),
            )
        }
    }
}

@Composable
fun AssistantInputArea(
    isLoading: Boolean,
    currentMode: AIMode,
    onModeChange: (AIMode) -> Unit,
    onSendMessage: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var isModeSelectorVisible by remember { mutableStateOf(false) }

    // Анимируем появление меню
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // Выезжающее меню режимов
        AnimatedVisibility(
            visible = isModeSelectorVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn() + expandVertically(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut() + shrinkVertically()
        ) {
            ModeSelector(
                currentMode = currentMode,
                onModeSelected = {
                    onModeChange(it)
                    isModeSelectorVisible = false
                }
            )
        }

        // Основная строка ввода
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // Серый контейнер (Поле ввода)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFFF5F5F5))
                    .defaultMinSize(minHeight = 56.dp)
            ) {
                // 1. КНОПКА ВЫБОРА РЕЖИМА (ТЕПЕРЬ СЛЕВА ВНУТРИ)
                IconButton(
                    onClick = { isModeSelectorVisible = !isModeSelectorVisible },
                    modifier = Modifier
                        .align(Alignment.BottomStart) // Прижимаем влево вниз
                        .padding(8.dp) // Отступы от края
                        .size(40.dp) // Размер как у кнопки отправки
                        .clip(CircleShape)
                        // Меняем цвет если меню открыто
                        .background(if (isModeSelectorVisible) Color(0xFFFF8C00) else Color.Transparent)
                ) {
                    Icon(
                        // Убедитесь, что иконка правильная (в исходном коде она обрезана, я поставил заглушку)
                        painter = painterResource(R.drawable.ic_rsu_improve),
                        contentDescription = "Mode",
                        // Меняем цвет иконки для контраста
                        tint = if (isModeSelectorVisible) Color.White else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // 2. ПОЛЕ ВВОДА
                BasicTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)
                        // ВАЖНО: увеличен start padding (было 20.dp, стало 48.dp), чтобы текст не наезжал на левую кнопку
                        .padding(start = 48.dp, top = 16.dp, bottom = 16.dp, end = 50.dp),
                    textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (inputText.isEmpty()) {
                                Text(
                                    text = stringResource(currentMode.titleRes),
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    maxLines = 1
                                )
                            }
                            innerTextField()
                        }
                    },
                    singleLine = false,
                    maxLines = 6,
                    enabled = !isLoading
                )

                // 3. КНОПКА ОТПРАВКИ (СПРАВА ВНУТРИ - без изменений)
                IconButton(
                    onClick = {
                        if (!isLoading) {
                            onSendMessage(inputText.trim())
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 8.dp, bottom = 8.dp, start = 8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isLoading) Color.Gray else Color(0xFFFF8C00)),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.ic_ai_assistant),
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Spacer и внешний IconButton удалены
        }
    }
}


@Composable
fun ModeSelector(
    currentMode: AIMode,
    onModeSelected: (AIMode) -> Unit
) {
    val modes = listOf(AIMode.Text, AIMode.Configuration, AIMode.SmartSearch)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Выберите режим работы",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )

        modes.forEach { mode ->
            val isSelected = currentMode::class == mode::class
            val backgroundColor = if (isSelected) Color(0xFFFFF3E0) else Color.White
            val borderColor = if (isSelected) Color(0xFFFF8C00) else Color(0xFFE0E0E0)
            val textColor = if (isSelected) Color(0xFFEF6C00) else Color.Black

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onModeSelected(mode) },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, borderColor),
                colors = CardDefaults.cardColors(containerColor = backgroundColor)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Иконка режима (можно добавить свои иконки в sealed class AIMode для красоты)
                    Icon(
                        painter = painterResource(
                            when (mode) {
                                is AIMode.Text -> R.drawable.ic_ai_assistant
                                is AIMode.Configuration -> R.drawable.ic_rsu_cpu // Замените на актуальную иконку ПК
                                is AIMode.SmartSearch -> R.drawable.ic_home_search // Замените на актуальную иконку поиска
                            }
                        ),
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = stringResource(mode.titleRes),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = textColor
                    )

                    if (isSelected) {
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_path), // Убедитесь что есть иконка галочки
                            contentDescription = "Selected",
                            tint = Color(0xFFFF8C00),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun MessageList(
    messages: List<ChatMessage>,
    isLoading: Boolean,
    listState: LazyListState,
    context: MainActivity,
) {
    LaunchedEffect(isLoading) {
        if (isLoading) {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size)
            }
        }
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(messages) { message ->
            MessageBubble(message, context)
        }

        if (isLoading) {
            item {
                TypingBubble()
            }
        }
    }
}

@Composable
fun EmptyChatPlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(start = 32.dp, end = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Привет!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Я готов помочь. Выберите режим и спросите меня о чем угодно!",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    context: MainActivity,
) {
    val backgroundColor = when {
        message.isError -> Color(0xFFFFEBEE)
        message.isUser -> Color(0xFFFF8C00)
        else -> Color(0xFFF5F5F5)
    }

    val contentColor = if (message.isUser) Color.White else Color.Black
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart

    val shape = if (message.isUser) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    }

    val alphaAnim = remember { Animatable(if (message.isUser) 1f else 0f) }

    LaunchedEffect(message) {
        if (!message.isUser) {
            alphaAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600)
            )
        }
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Column {
            Box(
                modifier = Modifier
                    .widthIn(max = 320.dp)
                    .graphicsLayer {
                        alpha = alphaAnim.value
                    }
                    .clip(shape)
                    .background(backgroundColor)
                    .padding(12.dp)
            ) {
                // Используем Column, чтобы разместить товары под текстом
                Column {
                    val customTypography = markdownTypography(
                        h1 = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        h2 = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        h3 = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        h4 = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        h5 = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        h6 = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        paragraph = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
                    )

                    // 1. Основной текст сообщения
                    if (message.isUser) {
                        Text(
                            text = message.text,
                            color = contentColor,
                            fontSize = 16.sp
                        )
                    } else {
                        Markdown(
                            content = message.text,
                            typography = customTypography
                        )
                    }
                }
            }
            // 2. Список товаров (если они есть и это сообщение от бота)
            // Предполагается, что в ChatMessage есть поле products
            if (!message.isUser && message.products?.isNotEmpty() == true) {
                Spacer(modifier = Modifier.height(12.dp))



                message.products.forEach { product ->
                    // Разделитель между товарами (если нужно)
                    Spacer(modifier = Modifier.height(8.dp))

                    // ВАШ ГОТОВЫЙ КОМПОНЕНТ
                    ProductItem(
                        title = product.model,
                        imageUrl = product.src,
                        price = product.price,
                        oldPrice = product.price * 1.2.toInt(),
                        rating = 4.8,
                        reviewsCount = 123,
                        onClick = { /* Обработка клика по товару */ }
                    )
                }
            }
        }
    }
}


@Composable
fun TypingBubble() {
    val dotColor = Color.Gray
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    // Анимация для трех точек
    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "dot1"
    )
    val offset2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, delayMillis = 100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "dot2"
    )
    val offset3 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, delayMillis = 200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "dot3"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
                .background(Color(0xFFF5F5F5))
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Dot(offset1, dotColor)
            Dot(offset2, dotColor)
            Dot(offset3, dotColor)
        }
    }
}

@Composable
fun Dot(offset: Float, color: Color) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .graphicsLayer { translationY = offset }
            .background(color, CircleShape)
    )
}
