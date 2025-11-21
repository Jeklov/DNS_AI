package com.jeklov.dns.ui.composables.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.jeklov.dns.ui.screens.Screens
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Временная константа для оранжевого цвета
private val OrangeRippleColor = Color(0xFFFF8C00)

@Composable
fun BottomBarUI(
    navigationController: NavHostController,
    screenItemsBar: List<Screens>,
    bottomBarState: MutableState<Boolean>
) {
    val navBackStackEntry by navigationController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    AnimatedVisibility(
        visible = bottomBarState.value,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        content = {
            Column(Modifier.fillMaxWidth()) {
                HorizontalDivider(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    thickness = 1.dp
                )

                BottomAppBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp,
                    modifier = Modifier.height(60.dp)
                ) {
                    screenItemsBar.forEach { screens ->
                        if (screens.showIconOnBottomBar) {
                            val isSelected = currentDestination?.route == screens.screen

                            val coroutineScope = rememberCoroutineScope()
                            val interactionSource = remember { MutableInteractionSource() }

                            // Анимация параметров круга
                            val circleRadius = remember { Animatable(0f) }
                            val circleAlpha = remember { Animatable(0f) }

                            // Состояние для цвета круга (чтобы помнить, каким цветом бахнули при клике)
                            var currentRippleColor by remember { mutableStateOf(Color.Black) }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable(
                                        interactionSource = interactionSource,
                                        indication = null,
                                        onClick = {
                                            currentRippleColor = if (isSelected) {
                                                OrangeRippleColor
                                            } else {
                                                Color.Black
                                            }

                                            coroutineScope.launch {
                                                circleRadius.snapTo(0f)
                                                circleAlpha.snapTo(0.1f)
                                                launch {
                                                    circleRadius.animateTo(
                                                        targetValue = 46f,
                                                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                                                    )
                                                }

                                                launch {
                                                    // Ждем 400мс ("притормаживаем" в раскрытом состоянии)
                                                    delay(400)
                                                    circleAlpha.animateTo(
                                                        targetValue = 0f,
                                                        animationSpec = tween(350)
                                                    )
                                                }
                                            }

                                            navigationController.navigate(screens.screen) {
                                                launchSingleTop = true
                                                popUpTo(navigationController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                restoreState = true
                                            }
                                        }
                                    )
                                    .drawBehind {
                                        if (circleAlpha.value > 0f) {
                                            drawCircle(
                                                color = currentRippleColor.copy(alpha = circleAlpha.value),
                                                radius = circleRadius.value.dp.toPx()
                                            )
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                screens.lottieRes?.let { resId ->
                                    val composition by rememberLottieComposition(
                                        LottieCompositionSpec.RawRes(resId)
                                    )

                                    val progress by animateFloatAsState(
                                        targetValue = if (isSelected) 1f else 0f,
                                        animationSpec = if (isSelected) {
                                            tween(durationMillis = 600, easing = LinearEasing)
                                        } else {
                                            snap()
                                        },
                                        label = "LottieProgress"
                                    )

                                    LottieAnimation(
                                        composition = composition,
                                        progress = { progress },
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
