package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// Local Noto Sans Bengali font family bundled in res/font
val NotoSansBengaliFontFamily = FontFamily(
    Font(R.font.noto_sans_bengali, FontWeight.Normal)
)

/**
 * Strict Typography Scale for Hardware Manager:
 * - Big money numbers: 22sp (lineHeight ~31sp, 1.4x)
 * - Screen title: 18sp (lineHeight ~25sp, 1.4x)
 * - Section heading: 15sp (lineHeight ~21sp, 1.4x)
 * - Normal text / Button: 14sp (lineHeight ~20sp, 1.4x)
 * - Small hint text: 12sp (lineHeight ~17sp, 1.4x)
 * - Minimum font size across whole app: 12sp
 */
object AppTypographyScale {
    val moneyNumber = TextStyle(
        fontFamily = NotoSansBengaliFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 31.sp,
        letterSpacing = 0.sp
    )

    val screenTitle = TextStyle(
        fontFamily = NotoSansBengaliFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 25.sp,
        letterSpacing = 0.sp
    )

    val sectionHeading = TextStyle(
        fontFamily = NotoSansBengaliFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 21.sp,
        letterSpacing = 0.sp
    )

    val normalText = TextStyle(
        fontFamily = NotoSansBengaliFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp
    )

    val buttonText = TextStyle(
        fontFamily = NotoSansBengaliFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp
    )

    val hintText = TextStyle(
        fontFamily = NotoSansBengaliFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.2.sp
    )

    val cardTitle = TextStyle(
        fontFamily = NotoSansBengaliFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 21.sp,
        letterSpacing = 0.sp
    )

    val bodySmall = TextStyle(
        fontFamily = NotoSansBengaliFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.2.sp
    )
}

val AppTypography = Typography(
    displayLarge = AppTypographyScale.moneyNumber,
    displayMedium = AppTypographyScale.moneyNumber,
    displaySmall = AppTypographyScale.moneyNumber,
    headlineLarge = AppTypographyScale.moneyNumber,
    headlineMedium = AppTypographyScale.moneyNumber,
    headlineSmall = AppTypographyScale.sectionHeading,
    titleLarge = AppTypographyScale.screenTitle,
    titleMedium = AppTypographyScale.screenTitle,
    titleSmall = AppTypographyScale.sectionHeading,
    bodyLarge = AppTypographyScale.normalText,
    bodyMedium = AppTypographyScale.normalText,
    bodySmall = AppTypographyScale.hintText,
    labelLarge = AppTypographyScale.buttonText,
    labelMedium = AppTypographyScale.hintText,
    labelSmall = AppTypographyScale.hintText
)
