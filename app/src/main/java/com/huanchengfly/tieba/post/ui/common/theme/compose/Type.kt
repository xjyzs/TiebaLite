package com.huanchengfly.tieba.post.ui.common.theme.compose

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val TiebaTypography = Typography(
    bodyLarge = TextStyle.Default.copy(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    titleMedium = TextStyle.Default.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp
    ),
    labelLarge = TextStyle.Default.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.15.sp
    )
)