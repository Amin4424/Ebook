package com.example.ebook.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Navy800,
    onPrimary = TextOnDark,
    primaryContainer = Navy600,
    onPrimaryContainer = TextOnDark,
    secondary = Gold500,
    onSecondary = Navy900,
    secondaryContainer = Gold200,
    onSecondaryContainer = Navy900,
    tertiary = Gold400,
    onTertiary = Navy900,
    background = OffWhite,
    onBackground = TextPrimary,
    surface = CardWhite,
    onSurface = TextPrimary,
    surfaceVariant = LightGray,
    onSurfaceVariant = TextSecondary,
    outline = TextSecondary,
)

private val DarkColorScheme = darkColorScheme(
    primary = Gold400,
    onPrimary = Navy900,
    primaryContainer = Navy700,
    onPrimaryContainer = TextOnDark,
    secondary = Gold500,
    onSecondary = Navy900,
    secondaryContainer = Navy700,
    onSecondaryContainer = Gold300,
    tertiary = Gold300,
    onTertiary = Navy900,
    background = DarkBackground,
    onBackground = TextOnDark,
    surface = DarkSurface,
    onSurface = TextOnDark,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextOnDarkSecondary,
    outline = TextOnDarkSecondary,
)

@Composable
fun EBookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = EBookTypography,
        content = content
    )
}