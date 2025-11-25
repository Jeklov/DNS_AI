package com.jeklov.dns.ui.screens.assistant

import android.app.Application
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jeklov.dns.R
import com.jeklov.dns.data.api.chat.models.ChatRequest
import com.jeklov.dns.data.api.chat.repository.ChatRepository
import com.jeklov.dns.data.api.chat.view.model.ChatViewModel
import com.jeklov.dns.data.api.chat.view.model.ChatViewModelProviderFactory
import com.jeklov.dns.data.util.Resource
import com.jeklov.dns.ui.screens.Screens
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography

// --- МОДЕЛИ ---
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isError: Boolean = false
)

// --- ENTRY POINT (ROUTE) ---
@Composable
fun AssistantPageUI(
    paddingValues: PaddingValues,
    navigationController: NavHostController,
    application: Application,
    prompt: String?,
) {
    // 1. Инициализация ViewModel
    val viewModel: ChatViewModel = viewModel(
        factory = ChatViewModelProviderFactory(application, ChatRepository())
    )

    // 2. Состояния
    val chatState by viewModel.chatToken.collectAsStateWithLifecycle()
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    // 3. Обработка ответов от сервера (Side Effects)
    LaunchedEffect(chatState) {
        when (val state = chatState) {
            is Resource.Success -> {
                val responseText = state.data.response
                // Проверка на дублирование (простая), чтобы не добавлять одно и то же при рекомпозиции
                if (messages.lastOrNull()?.text != responseText) {
                    messages.add(ChatMessage(responseText, isUser = false))
                }
            }

            is Resource.Error -> {
                val errorMsg = state.exception
                messages.add(ChatMessage(errorMsg, isUser = false, isError = true))
                Toast.makeText(context, "Ошибка: $errorMsg", Toast.LENGTH_SHORT).show()
            }

            else -> { /* Loading или Idle обрабатываются в UI через флаг isLoading */
            }
        }
    }

    // 4. Автопрокрутка вниз при добавлении сообщений
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            // Скроллим к последнему элементу. Если идет загрузка, UI сам проскроллит до "пузыря"
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // 5. Функция отправки
    fun sendMessage(text: String) {
        if (text.isBlank()) return
        messages.add(ChatMessage(text, true))
        viewModel.chat(ChatRequest(text))
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
        // Лог для отладки (как вы просили ранее)
        val currentRoute = navigationController.currentBackStackEntry?.destination?.route
        val targetRoute = navigationController.previousBackStackEntry?.destination?.route
        android.util.Log.d("NavigationLog", "Back Action. Current: $currentRoute, Target: $targetRoute")

        if (targetRoute == Screens.SearchPage.screen) {
            navigationController.navigate(Screens.MainPage.screen) {
                popUpTo(0)
            }
        } else {
            try {
                navigationController.popBackStack()
            } catch (e: Exception) {
                // Если стек пуст или ошибка, на всякий случай идем на главную
                navigationController.navigate(Screens.MainPage.screen) { popUpTo(0) }
            }
        }
    }

    // 1. Перехватываем системный жест/кнопку "Назад"
    BackHandler(enabled = true, onBack = handleBack)

    // 7. Отображение экрана
    AssistantScreen(
        paddingValues = paddingValues,
        messages = messages,
        isLoading = chatState is Resource.Loading,
        listState = listState,
        // 2. Передаем ту же самую функцию в кнопку UI
        onBackClick = handleBack,
        onHistoryClick = { navigationController.navigate(Screens.AssistantHistoryPage.screen) },
        onSendMessage = { sendMessage(it) }
    )
}

// --- ОСНОВНОЙ ЭКРАН (UI) ---
@Composable
fun AssistantScreen(
    paddingValues: PaddingValues,
    messages: List<ChatMessage>,
    isLoading: Boolean,
    listState: LazyListState,
    onBackClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSendMessage: (String) -> Unit
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
                        listState = listState
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
                painter = painterResource(R.drawable.ic_profile_quit),
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
    onSendMessage: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Серый контейнер
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xFFF5F5F5))
                // Минимальная высота, чтобы поле выглядело аккуратно даже пустым
                .defaultMinSize(minHeight = 56.dp)
        ) {
            // Поле ввода текста
            BasicTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart)
                    // Важно: end = 60.dp, чтобы текст не уходил под кнопку отправки
                    .padding(start = 20.dp, top = 16.dp, bottom = 16.dp, end = 60.dp),
                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                // ImeAction.Default позволяет делать перенос строки (Enter)
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (inputText.isEmpty()) {
                            Text(
                                "Что сегодня будем искать?",
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                },
                singleLine = false, // Разрешаем многострочность
                maxLines = 6,       // Ограничиваем высоту поля 6 строками
                enabled = !isLoading
            )

            // Кнопка отправки
            IconButton(
                onClick = {
                    if (!isLoading) {
                        onSendMessage(inputText.trim())
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd) // Прижимаем кнопку к правому нижнему углу
                    .padding(end = 8.dp, bottom = 8.dp) // Отступы справа и снизу по 12dp
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isLoading) Color.Gray else Color(0xFFFF8C00)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_ai_assistant),
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun MessageList(
    messages: List<ChatMessage>,
    isLoading: Boolean,
    listState: LazyListState
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
            MessageBubble(message)
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
            text = "Я готов помочь. Спроси меня о чем угодно!",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
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

    // 1. Создаем переменную для анимации прозрачности
    val alphaAnim = remember { Animatable(if (message.isUser) 1f else 0f) }

    // 2. Запускаем анимацию "проявления" только для сообщений бота
    LaunchedEffect(message) {
        if (!message.isUser) {
            alphaAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600)
            )
        }
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
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
            if (message.isUser) {
                Text(text = message.text, color = contentColor, fontSize = 16.sp)
            } else {
                MaterialTheme(
                    colorScheme = MaterialTheme.colorScheme.copy(
                        onSurface = contentColor,
                        onSurfaceVariant = contentColor,
                        surface = Color.Transparent
                    )
                ) {
                    val customTypography = markdownTypography(
                        h1 = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        h2 = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        h3 = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        h4 = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        h5 = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        h6 = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        paragraph = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)
                    )

                    Markdown(
                        content = message.text,
                        typography = customTypography,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}


@Composable
fun TypingBubble() {
    val dotSize = 8.dp
    val spaceBetween = 4.dp
    val travelDistance = 6.dp
    val dotColor = Color.Gray

    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0.0f at 0 using LinearOutSlowInEasing
                -travelDistance.value at 300 using LinearOutSlowInEasing
                0.0f at 600 using LinearOutSlowInEasing
                0.0f at 1200 using LinearOutSlowInEasing
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "dot1"
    )
    val offset2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0.0f at 0 using LinearOutSlowInEasing
                0.0f at 200 using LinearOutSlowInEasing
                -travelDistance.value at 500 using LinearOutSlowInEasing
                0.0f at 800 using LinearOutSlowInEasing
                0.0f at 1200 using LinearOutSlowInEasing
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "dot2"
    )
    val offset3 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0.0f at 0 using LinearOutSlowInEasing
                0.0f at 400 using LinearOutSlowInEasing
                -travelDistance.value at 700 using LinearOutSlowInEasing
                0.0f at 1000 using LinearOutSlowInEasing
                0.0f at 1200 using LinearOutSlowInEasing
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "dot3"
    )

    val shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    val backgroundColor = Color(0xFFF5F5F5)

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .background(backgroundColor)
                .animateContentSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(24.dp)
            ) {
                Dot(offset1, dotSize, dotColor)
                Spacer(Modifier.width(spaceBetween))
                Dot(offset2, dotSize, dotColor)
                Spacer(Modifier.width(spaceBetween))
                Dot(offset3, dotSize, dotColor)
            }
        }
    }
}

@Composable
fun Dot(offset: Float, size: androidx.compose.ui.unit.Dp, color: Color) {
    Box(
        modifier = Modifier
            .offset(y = offset.dp)
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}
