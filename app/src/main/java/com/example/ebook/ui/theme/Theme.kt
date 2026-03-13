package com.example.ebook.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.staticCompositionLocalOf

enum class ThemeMode { Light, Dark, System }

val LocalThemeMode = staticCompositionLocalOf { mutableStateOf(ThemeMode.System) }

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
    themeMode: ThemeMode = ThemeMode.System,
    content: @Composable () -> Unit
) {
    val useDark = when (themeMode) {
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
        ThemeMode.System -> darkTheme
    }
    val colorScheme = if (useDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = EBookTypography,
        content = content
    )
}