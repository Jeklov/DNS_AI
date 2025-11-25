package com.jeklov.dns.ui.screens.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.jeklov.dns.R


// --- Цвета из скриншота ---
private val DnsOrange = Color(0xFFFC8507)
private val DnsPriceOrange = Color(0xFFFF8C00) // Чуть ярче для цены
private val DnsLinkBlue = Color(0xFF0060A0)
private val DnsTextBlack = Color(0xFF333333)
private val DnsLightGrayBg = Color(0xFFF7F7F7) // Фон блока рассрочки
private val DnsBorderGray = Color(0xFFE0E0E0)
private val StarYellow = Color(0xFFFFC107)
private val ReliabilityGreen = Color(0xFF4CAF50)
private val BadgeCyan = Color(0xFF00BCD4)
private val PromoPinkBg = Color(0xFFFFF0F0) // Фон шильдика рассрочки
private val PromoRedText = Color(0xFFD85858)

@Composable
fun ProductItem(
    title: String = "6.3\" Смартфон Apple iPhone 17 Pro 256 ГБ синий [ядер - 6, 12 ГБ, 1 SIM, Super Retina XDR, 2...]",
    imageUrl: String = "https://c.dns-shop.ru/thumb/st4/fit/320/250/559e7426443358c17008723764971307/2b01840293808d0273144092095647267422452f3753114399751812849c4f0b.jpg",
    price: Int = 138499,
    oldPrice: Int? = 162999,
    rating: Double = 4.8,
    reviewsCount: Int = 128,
    onClick: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)

    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
        ) {
            // Используем BoxWithConstraints для определения доступной ширины
            BoxWithConstraints(modifier = Modifier.padding(12.dp)) {
                val width = maxWidth

                // --- Адаптивная логика ---
                // Если ширина карточки меньше 360dp (узкий экран или сетка), уменьшаем размеры
                val isCompact = width < 360.dp

                // Вычисляем размеры шрифтов (УВЕЛИЧИЛ размеры titleFontSize)
                val titleFontSize = if (isCompact) 13.sp else 15.sp
                val priceFontSize = if (isCompact) 15.sp else 18.sp
                val installmentFontSize = if (isCompact) 10.sp else 12.sp

                // Размеры кнопок
                val buttonHeight = if (isCompact) 36.dp else 44.dp
                val buttonIconSize = if (isCompact) 18.dp else 24.dp
                val buttonTextSize = if (isCompact) 13.sp else 16.sp

                // Размеры изображения (Изменил высоту для соотношения близкого к 1:2 по контенту, но
                // обычно в UI это выглядит как узкий прямоугольник.
                // Если имелось в виду ширина 1 к высоте 2, то для ширины 80 высота будет 160)
                val imageWidth = if (isCompact) 40.dp else 60.dp
                val imageHeight = imageWidth * 2 // Соотношение 1 к 2

                Column {
                    // ================= Верхняя часть: Фото + Описание =================
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min) // Важно для растягивания колонки описания по высоте картинки
                    ) {
                        // --- Блок изображения ---
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(imageHeight)
                        ) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.Fit
                            )
                            Surface(
                                color = BadgeCyan,
                                shape = CircleShape,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 8.dp, y = (-4).dp)
                                    .size(if (isCompact) 24.dp else 32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "IP68",
                                        color = Color.White,
                                        fontSize = if (isCompact) 8.sp else 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // --- Описание и промо ---
                        Column(
                            modifier = Modifier
                                .weight(2f), // Занимаем все оставшееся место по ширине
                            verticalArrangement = Arrangement.SpaceBetween // Магнитим контент к краям (верх/низ)
                        ) {
                            Text(
                                text = title,
                                maxLines = 6, // Увеличили кол-во строк, т.к. блок стал выше
                                overflow = TextOverflow.Ellipsis,
                                fontSize = titleFontSize,
                                lineHeight = titleFontSize * 1.3,
                                color = DnsTextBlack,
                                modifier = Modifier.weight(
                                    1f,
                                    fill = false
                                ) // Текст занимает верх, но не растягивает лишнее
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            // Акция теперь прибита к низу благодаря Arrangement.SpaceBetween у родительской Column
                            Text(
                                text = "Рассрочка 0-0-24 или Выгода 24 500",
                                fontSize = if (isCompact) 10.sp else 12.sp, // Чуть увеличил шрифт акции
                                color = PromoRedText,
                                lineHeight = 14.sp,
                                modifier = Modifier
                                    .background(PromoPinkBg, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ================= Блок "Чипов" (LazyRow) =================
                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Чип "Сравнить"
                        item {
                            OutlinedContainer {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Вместо тяжелого Checkbox рисуем аккуратный квадрат
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .border(
                                                width = 1.dp, // Тонкая рамка
                                                color = Color(0xFFC4C4C4), // Светло-серый цвет, не бросается в глаза
                                                shape = RoundedCornerShape(4.dp) // Скругленные углы
                                            )
                                        // Если нужно сделать кликабельным, добавьте .clickable { ... }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Сравнить", fontSize = 13.sp, color = DnsTextBlack)
                                }
                            }
                        }

                        // 2. Чип "Рейтинг"
                        item {
                            OutlinedContainer {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    repeat(5) { index ->
                                        // Логика: если рейтинг 4.0, то 4 желтых, 1 серая
                                        val tint = if (index < 4) StarYellow else Color(0xFFE0E0E0)
                                        Icon(
                                            painter = painterResource(R.drawable.ic_home_star),
                                            contentDescription = null,
                                            tint = tint,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        reviewsCount.toString(),
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }

                        // 3. Чип "Надёжность"
                        item {
                            OutlinedContainer {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_profile_security),
                                        contentDescription = null,
                                        tint = ReliabilityGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Отличная надёжность",
                                        fontSize = 13.sp,
                                        color = DnsTextBlack
                                    )
                                }
                            }
                        }
                    }



                    Spacer(modifier = Modifier.height(12.dp))

                    // ================= Цена и Кнопки =================
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(buttonHeight)
                                .background(DnsLightGrayBg, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                Text(
                                    text = "${formatPrice(price)} ₽",
                                    fontSize = priceFontSize,
                                    fontWeight = FontWeight.Bold,
                                    color = DnsPriceOrange,
                                    lineHeight = priceFontSize
                                )

                                Text(
                                    text = "или ${formatPrice(price / 12)}/мес",
                                    fontSize = installmentFontSize,
                                    color = DnsTextBlack,
                                    lineHeight = installmentFontSize
                                )
                            }

                            if (width > 280.dp) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_profile_about),
                                    contentDescription = "Info",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        OutlinedButton(
                            onClick = {},
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, DnsBorderGray),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(buttonHeight)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_profile_favorites),
                                contentDescription = "Favorite",
                                tint = Color.Gray,
                                modifier = Modifier.size(buttonIconSize)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Button(
                            onClick = { /* TODO */ },
                            colors = ButtonDefaults.buttonColors(containerColor = DnsOrange),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            modifier = Modifier
                                .height(buttonHeight)
                                .widthIn(min = if (isCompact) 80.dp else 100.dp)
                        ) {
                            Text("Купить", fontSize = buttonTextSize, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ================= Подвал =================
                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item { FooterInfoItem("В наличии:", "в 23 магазинах") }
                        item { FooterInfoItem("Пункты выдачи:", "доступны") }
                        item { FooterInfoItem("Доставим:", "Завтра") }
                    }
                }
            }
        }
    }
}

// ... (Остальные функции без изменений)
@Composable
private fun OutlinedContainer(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .border(1.dp, DnsBorderGray, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        content()
    }
}

@Composable
private fun FooterInfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(DnsLightGrayBg, RoundedCornerShape(8.dp))
            .padding(vertical = 10.dp, horizontal = 12.dp)
            .widthIn(min = 100.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF777777),
            lineHeight = 12.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            color = DnsLinkBlue,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun formatPrice(price: Int): String {
    return String.format("%,d", price).replace(',', ' ')
}
