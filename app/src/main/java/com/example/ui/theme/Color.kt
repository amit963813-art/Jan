package com.example.ui.theme

import androidx.compose.ui.graphics.Color

enum class NeonThemePreset(
    val displayName: String,
    val primaryNeon: Color,
    val secondaryNeon: Color,
    val accentCyan: Color,
    val darkBg: Color,
    val darkSurface: Color,
    val darkCard: Color
) {
    NEON_PURPLE_AURORA(
        displayName = "Neon Purple/Pink Aurora",
        primaryNeon = Color(0xFFD946EF),
        secondaryNeon = Color(0xFF8B5CF6),
        accentCyan = Color(0xFF00F5D4),
        darkBg = Color(0xFF090414),
        darkSurface = Color(0xFF130926),
        darkCard = Color(0xFF1D1036)
    ),
    CYBER_EMERALD(
        displayName = "Cyber Emerald",
        primaryNeon = Color(0xFF10B981),
        secondaryNeon = Color(0xFF06B6D4),
        accentCyan = Color(0xFFA3E635),
        darkBg = Color(0xFF04120E),
        darkSurface = Color(0xFF09221B),
        darkCard = Color(0xFF103027)
    ),
    SUNSET_BLAZE(
        displayName = "Sunset Blaze",
        primaryNeon = Color(0xFFF43F5E),
        secondaryNeon = Color(0xFFF97316),
        accentCyan = Color(0xFFFACC15),
        darkBg = Color(0xFF140509),
        darkSurface = Color(0xFF260A12),
        darkCard = Color(0xFF36111B)
    ),
    OCEAN_BLUE(
        displayName = "Ocean Blue",
        primaryNeon = Color(0xFF38BDF8),
        secondaryNeon = Color(0xFF6366F1),
        accentCyan = Color(0xFF2DD4BF),
        darkBg = Color(0xFF040B16),
        darkSurface = Color(0xFF0A162B),
        darkCard = Color(0xFF112240)
    ),
    ROYAL_GOLD(
        displayName = "Royal Gold",
        primaryNeon = Color(0xFFF59E0B),
        secondaryNeon = Color(0xFFD946EF),
        accentCyan = Color(0xFFFDE047),
        darkBg = Color(0xFF120D04),
        darkSurface = Color(0xFF221908),
        darkCard = Color(0xFF33260E)
    )
}
