package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = KrishiDarkPrimary,
    onPrimary = Color(0xFF003A0F),
    primaryContainer = KrishiGreenPrimary,
    onPrimaryContainer = Color(0xFFA5D6A7),
    secondary = KrishiDarkSecondary,
    tertiary = KrishiDarkTertiary,
    background = KrishiDarkBackground,
    surface = KrishiDarkSurface,
    surfaceVariant = KrishiDarkSurfaceVariant,
    onBackground = Color(0xFFE2E4DE),
    onSurface = Color(0xFFE2E4DE),
    outline = Color(0xFF8C9388)
)

private val LightColorScheme = lightColorScheme(
    primary = KrishiGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = KrishiGreenContainer,
    onPrimaryContainer = KrishiOnGreenContainer,
    secondary = KrishiGreenSecondary,
    onSecondary = Color.White,
    tertiary = KrishiGold,
    onTertiary = Color.White,
    background = KrishiBackground,
    onBackground = KrishiOnSurface,
    surface = KrishiSurface,
    onSurface = KrishiOnSurface,
    surfaceVariant = KrishiSurfaceVariant,
    outline = KrishiOutline
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep custom agricultural branding
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
