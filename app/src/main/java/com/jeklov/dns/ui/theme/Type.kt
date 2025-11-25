package com.jeklov.dns.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.jeklov.dns.R

// 1. Определяем семейство PT Sans
val PtSansFontFamily = FontFamily(
    Font(R.font.ptsans_regular, FontWeight.Normal),
    Font(R.font.ptsans_bold, FontWeight.Bold),
    Font(R.font.ptsans_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.ptsans_bolditalic, FontWeight.Bold, FontStyle.Italic)
)

// 2. Определяем шрифт для цифр (Roboto)
val RobotoNumbersFontFamily = FontFamily(
    Font(R.font.roboto_medium_numbers, FontWeight.Medium) // Или Normal, если это единственное начертание
)

// 3. Настраиваем типографику по умолчанию на PT Sans
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = PtSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // Вам нужно продублировать fontFamily = PtSansFontFamily для всех остальных стилей (titleLarge, labelSmall и т.д.)
    // Чтобы не писать много кода, можно использовать функцию-помощник ниже.
    titleLarge = TextStyle(
        fontFamily = PtSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    /* ... остальные стили ... */
)

// Хелпер, если нужно быстро применить шрифт ко всем стилям MaterialTheme
fun Typography.applyFontFamily(fontFamily: FontFamily): Typography {
    return this.copy(
        displayLarge = this.displayLarge.copy(fontFamily = fontFamily),
        displayMedium = this.displayMedium.copy(fontFamily = fontFamily),
        displaySmall = this.displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = this.headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = this.headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = this.headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = this.titleLarge.copy(fontFamily = fontFamily),
        titleMedium = this.titleMedium.copy(fontFamily = fontFamily),
        titleSmall = this.titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = this.bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = this.bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = this.bodySmall.copy(fontFamily = fontFamily),
        labelLarge = this.labelLarge.copy(fontFamily = fontFamily),
        labelMedium = this.labelMedium.copy(fontFamily = fontFamily),
        labelSmall = this.labelSmall.copy(fontFamily = fontFamily)
    )
}
