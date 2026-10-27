package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun KayraTheme(
    preset: NeonThemePreset = NeonThemePreset.NEON_PURPLE_AURORA,
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = preset.primaryNeon,
            onPrimary = Color.White,
            primaryContainer = preset.darkCard,
            onPrimaryContainer = preset.primaryNeon,
            secondary = preset.secondaryNeon,
            onSecondary = Color.White,
            secondaryContainer = preset.darkSurface,
            onSecondaryContainer = preset.accentCyan,
            tertiary = preset.accentCyan,
            background = preset.darkBg,
            onBackground = Color(0xFFF5F0FF),
            surface = preset.darkSurface,
            onSurface = Color(0xFFF5F0FF),
            surfaceVariant = preset.darkCard,
            onSurfaceVariant = Color(0xFFCBB8E8),
            error = Color(0xFFFF4D6D)
        )
    } else {
        lightColorScheme(
            primary = preset.primaryNeon,
            onPrimary = Color.White,
            primaryContainer = preset.primaryNeon.copy(alpha = 0.14f),
            onPrimaryContainer = Color(0xFF1F093D),
            secondary = preset.secondaryNeon,
            onSecondary = Color.White,
            secondaryContainer = preset.secondaryNeon.copy(alpha = 0.14f),
            onSecondaryContainer = Color(0xFF1F093D),
            tertiary = preset.accentCyan,
            background = Color(0xFFF8F5FF),
            onBackground = Color(0xFF16082C),
            surface = Color.White,
            onSurface = Color(0xFF16082C),
            surfaceVariant = Color(0xFFEDE5FF),
            onSurfaceVariant = Color(0xFF49366B),
            error = Color(0xFFD90429)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
