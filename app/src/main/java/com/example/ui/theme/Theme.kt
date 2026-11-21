package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private fun lightSchemeFor(palette: AppColorPalette): ColorScheme = when (palette) {
    AppColorPalette.EMERALD_GOLD -> lightColorScheme(
        primary = EmeraldPrimary,
        onPrimary = EmeraldOnPrimary,
        primaryContainer = EmeraldContainer,
        onPrimaryContainer = EmeraldOnContainer,
        secondary = GoldSecondary,
        onSecondary = GoldOnSecondary,
        secondaryContainer = GoldContainer,
        onSecondaryContainer = GoldOnContainer,
        tertiary = AegeanTertiary,
        tertiaryContainer = AegeanContainer,
        onTertiaryContainer = AegeanOnContainer,
        background = SilkBackground,
        onBackground = SilkOnSurface,
        surface = SilkSurface,
        onSurface = SilkOnSurface,
        surfaceVariant = SilkSurfaceVariant,
        onSurfaceVariant = SilkMuted,
        outline = SilkOutline,
        error = AlertRed,
        errorContainer = AlertRedContainer
    )

    AppColorPalette.SAPPHIRE_CORAL -> lightColorScheme(
        primary = SapphirePrimary,
        onPrimary = SapphireOnPrimary,
        primaryContainer = SapphireContainer,
        onPrimaryContainer = SapphireOnContainer,
        secondary = CoralSecondary,
        onSecondary = CoralOnSecondary,
        secondaryContainer = CoralContainer,
        onSecondaryContainer = CoralOnContainer,
        tertiary = TealTertiary,
        tertiaryContainer = TealContainer,
        onTertiaryContainer = TealOnContainer,
        background = PearlBackground,
        onBackground = PearlOnSurface,
        surface = PearlSurface,
        onSurface = PearlOnSurface,
        surfaceVariant = PearlSurfaceVariant,
        onSurfaceVariant = PearlMuted,
        outline = PearlOutline,
        error = AlertRed,
        errorContainer = AlertRedContainer
    )

    AppColorPalette.OLIVE_COPPER -> lightColorScheme(
        primary = OlivePrimary,
        onPrimary = OliveOnPrimary,
        primaryContainer = OliveContainer,
        onPrimaryContainer = OliveOnContainer,
        secondary = CopperSecondary,
        onSecondary = CopperOnSecondary,
        secondaryContainer = CopperContainer,
        onSecondaryContainer = CopperOnContainer,
        tertiary = OchreTertiary,
        tertiaryContainer = OchreContainer,
        onTertiaryContainer = OchreOnContainer,
        background = SandBackground,
        onBackground = SandOnSurface,
        surface = SandSurface,
        onSurface = SandOnSurface,
        surfaceVariant = SandSurfaceVariant,
        onSurfaceVariant = SandMuted,
        outline = SandOutline,
        error = AlertRed,
        errorContainer = AlertRedContainer
    )
}

private fun darkSchemeFor(palette: AppColorPalette): ColorScheme = when (palette) {
    AppColorPalette.EMERALD_GOLD -> darkColorScheme(
        primary = Color(0xFF5AD4C0),
        onPrimary = Color(0xFF003730),
        primaryContainer = Color(0xFF0A4D45),
        onPrimaryContainer = Color(0xFFD4EFE9),
        secondary = Color(0xFFF0BE63),
        onSecondary = Color(0xFF3E2600),
        secondaryContainer = Color(0xFF5A3B06),
        onSecondaryContainer = Color(0xFFFBE9C8),
        tertiary = Color(0xFF83C5E2),
        tertiaryContainer = Color(0xFF173D4E),
        onTertiaryContainer = Color(0xFFD8ECF5),
        background = DarkBgEmerald,
        onBackground = Color(0xFFEAF0EE),
        surface = DarkSurfaceEmerald,
        onSurface = Color(0xFFEAF0EE),
        surfaceVariant = DarkSurfaceVarEmerald,
        onSurfaceVariant = Color(0xFFA0AEA9),
        outline = DarkOutlineEmerald,
        error = Color(0xFFF28B82),
        errorContainer = Color(0xFF5C1E18)
    )

    AppColorPalette.SAPPHIRE_CORAL -> darkColorScheme(
        primary = Color(0xFF7EC4E8),
        onPrimary = Color(0xFF092E42),
        primaryContainer = Color(0xFF16415C),
        onPrimaryContainer = Color(0xFFD6E9F5),
        secondary = Color(0xFFF39C7D),
        onSecondary = Color(0xFF4A1809),
        secondaryContainer = Color(0xFF6B2A15),
        onSecondaryContainer = Color(0xFFFCE3DA),
        tertiary = Color(0xFF70D1CA),
        tertiaryContainer = Color(0xFF174A46),
        onTertiaryContainer = Color(0xFFD5F0EE),
        background = DarkBgSapphire,
        onBackground = Color(0xFFE9EFF5),
        surface = DarkSurfaceSapphire,
        onSurface = Color(0xFFE9EFF5),
        surfaceVariant = DarkSurfaceVarSapphire,
        onSurfaceVariant = Color(0xFFA1B0BF),
        outline = DarkOutlineSapphire,
        error = Color(0xFFF28B82),
        errorContainer = Color(0xFF5C1E18)
    )

    AppColorPalette.OLIVE_COPPER -> darkColorScheme(
        primary = Color(0xFF9EC7A2),
        onPrimary = Color(0xFF152918),
        primaryContainer = Color(0xFF2D4130),
        onPrimaryContainer = Color(0xFFDCE8DC),
        secondary = Color(0xFFEB9F6A),
        onSecondary = Color(0xFF421F06),
        secondaryContainer = Color(0xFF613414),
        onSecondaryContainer = Color(0xFFF9E4D4),
        tertiary = Color(0xFFD6BC83),
        tertiaryContainer = Color(0xFF4A3B1B),
        onTertiaryContainer = Color(0xFFF2E8D2),
        background = DarkBgOlive,
        onBackground = Color(0xFFECEBE4),
        surface = DarkSurfaceOlive,
        onSurface = Color(0xFFECEBE4),
        surfaceVariant = DarkSurfaceVarOlive,
        onSurfaceVariant = Color(0xFFA9A89C),
        outline = DarkOutlineOlive,
        error = Color(0xFFF28B82),
        errorContainer = Color(0xFF5C1E18)
    )
}

private val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@Composable
fun SyriaPosTheme(
    palette: AppColorPalette = AppColorPalette.EMERALD_GOLD,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val colorScheme = if (isDark) darkSchemeFor(palette) else lightSchemeFor(palette)
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
