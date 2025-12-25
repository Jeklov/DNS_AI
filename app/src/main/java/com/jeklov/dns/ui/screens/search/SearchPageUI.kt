package com.jeklov.dns.ui.screens.search

import android.app.Application
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.jeklov.dns.R
import com.jeklov.dns.data.api.ai.chat.models.AIMode
import com.jeklov.dns.ui.screens.Screens

@Composable
fun SearchPageUI(
    paddingValues: PaddingValues,
    navigationController: NavHostController,
    application: Application,
) {
    // Состояния для строки поиска и переключателя
    var searchQuery by remember { mutableStateOf("") }
    var isAssistantEnabled by remember { mutableStateOf(false) }

    // Requester для управления фокусом клавиатуры
    val focusRequester = remember { FocusRequester() }

    // Функция для выполнения поиска и навигации
    fun performSearch() {
        if (searchQuery.isNotBlank()) {
            val route = if (isAssistantEnabled) {
                Screens.AssistantPage.screen + "/" + searchQuery + "/" + AIMode.SmartSearch.name // Замените на ваш реальный роут для ИИ
            } else {
                Screens.ProductListPage.screen + "/" + searchQuery // Замените на ваш реальный роут каталога/поиска
            }
            navigationController.navigate(route)
        }
    }

    // Автоматически запрашиваем фокус при входе на экран
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            )
    ) {
        // --- Верхняя панель поиска (назад + поле ввода) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = if (isAssistantEnabled) Alignment.Bottom else Alignment.CenterVertically
        ) {
            // Кнопка Назад
            // Выравниваем кнопку по низу, если поле расширено, чтобы она не "улетала" вверх
            Box(
                modifier = Modifier
                    .height(48.dp), // Фиксируем высоту контейнера иконки
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back_black_24),
                    contentDescription = "Back",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { navigationController.popBackStack() }
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Поле ввода
            BasicTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 48.dp) // Минимальная высота, а не фиксированная
                    .focusRequester(focusRequester)
                    .background(
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(10.dp)
                    )
                    // Анимация изменения размера контейнера
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
                // Если включен ассистент - многострочный ввод (до 5 строк), иначе 1 строка
                singleLine = !isAssistantEnabled,
                maxLines = if (isAssistantEnabled) 5 else 1,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black
                ),
                cursorBrush = SolidColor(Color.Black),
                // Настройка клавиатуры
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { performSearch() }
                ),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth() // Заполняем ширину
                            .padding(horizontal = 12.dp, vertical = 12.dp), // Отступы внутри поля
                        verticalAlignment = Alignment.Top // Иконки центрируем по вертикали внутри Row (но так как это multi-line, поведение зависит от контента)
                    ) {
                        // Иконка лупы всегда сверху, если поле большое
                        Box(
                            modifier = Modifier.height(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_home_search),
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = if (isAssistantEnabled) "Спросите помощника..." else "Искать в DNS",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                            }
                            innerTextField()
                        }
                        if (searchQuery.isNotEmpty()) {
                            Box(
                                modifier = Modifier.height(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_cross),
                                    contentDescription = "Clear",
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .clickable { searchQuery = "" }
                                        .size(24.dp)
                                )
                            }
                        }
                    }
                }
            )
        }

        // --- Контент (скроллируемый) ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // 1. Умный помощник
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_ai_assistant),
                        contentDescription = "AI",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    // Анимация смены текста
                    AnimatedContent(
                        targetState = isAssistantEnabled,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "TextFadeAnimation"
                    ) { enabled ->
                        Text(
                            text = if (enabled) "Умный помощник включен, спрашивайте!" else "Попробуй умного помощника",
                            fontSize = 18.sp,
                            color = Color.Black.copy(alpha = 0.8f)
                        )
                    }
                }

                Switch(
                    checked = isAssistantEnabled,
                    onCheckedChange = { isAssistantEnabled = it },
                    colors = SwitchDefaults.colors(
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFE0E0E0),
                        uncheckedBorderColor = Color.Transparent,
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFFFFA500)
                    ),
                    modifier = Modifier.height(24.dp)
                )
            }
        }
    }
}
