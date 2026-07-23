package com.example.witube.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import com.example.witube.R

val Nunito = FontFamily(
    Font(R.font.nunito_black, FontWeight.Normal),
    Font(R.font.nunito_bold, FontWeight.Bold)
)

val baseline = Typography()

val Typography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = Nunito),
    displayMedium = baseline.displayMedium.copy(fontFamily = Nunito),
    displaySmall = baseline.displaySmall.copy(fontFamily = Nunito),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = Nunito),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = Nunito),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = Nunito),
    titleLarge = baseline.titleLarge.copy(fontFamily = Nunito),
    titleMedium = baseline.titleMedium.copy(fontFamily = Nunito),
    titleSmall = baseline.titleSmall.copy(fontFamily = Nunito),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = Nunito),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = Nunito),
    bodySmall = baseline.bodySmall.copy(fontFamily = Nunito),
    labelLarge = baseline.labelLarge.copy(fontFamily = Nunito),
    labelMedium = baseline.labelMedium.copy(fontFamily = Nunito),
    labelSmall = baseline.labelSmall.copy(fontFamily = Nunito),
)
