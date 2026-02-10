package com.igadgets.app

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val vazirmatnFamily = FontFamily(
    Font(R.font.vazirmatn_regular, FontWeight.Normal),
    Font(R.font.vazirmatn_bold, FontWeight.Bold)
)

val nastaliqFamily = FontFamily(
    Font(R.font.nastaliq, FontWeight.Normal)
)

val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = vazirmatnFamily),
    displayMedium = TextStyle(fontFamily = vazirmatnFamily),
    displaySmall = TextStyle(fontFamily = vazirmatnFamily),
    headlineLarge = TextStyle(fontFamily = vazirmatnFamily),
    headlineMedium = TextStyle(
        fontFamily = vazirmatnFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = vazirmatnFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = vazirmatnFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = vazirmatnFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    titleSmall = TextStyle(fontFamily = vazirmatnFamily),
    bodyLarge = TextStyle(
        fontFamily = vazirmatnFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(fontFamily = vazirmatnFamily),
    bodySmall = TextStyle(fontFamily = vazirmatnFamily),
    labelLarge = TextStyle(fontFamily = vazirmatnFamily),
    labelMedium = TextStyle(fontFamily = vazirmatnFamily),
    labelSmall = TextStyle(
        fontFamily = vazirmatnFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp
    )
)
