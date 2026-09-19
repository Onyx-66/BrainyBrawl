package com.brainybrawl.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Purple, onPrimary = Color.White, secondary = Cyan, onSecondary = Ink,
    tertiary = Positive, onTertiary = Ink, background = Navy, onBackground = Color.White,
    surface = Panel, onSurface = Color.White, surfaceVariant = PanelRaised,
    onSurfaceVariant = Muted, error = Negative, onError = Ink, outline = Cyan.copy(alpha = .45f)
)
private val LightColors = lightColorScheme(
    primary = Color(0xFF5737D5), onPrimary = Color.White, secondary = Color(0xFF00658A),
    onSecondary = Color.White, tertiary = Color(0xFF006B4E), onTertiary = Color.White,
    background = Color(0xFFEEF5FF), onBackground = Ink, surface = Color.White,
    onSurface = Ink, surfaceVariant = Color(0xFFDCEAFE), onSurfaceVariant = Color(0xFF38577A),
    error = Color(0xFFB51F42), onError = Color.White, outline = Color(0xFF6685AB)
)

@Composable
fun BrainyBrawlTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography, content = content)
}
