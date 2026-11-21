package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private fun lightSchemeFor(palette: AppColorPalette): ColorScheme = when (palette) {
    AppColorPalette.MATERIAL_YOU -> lightColorScheme(
        primary = Color(0xFF3F5F90),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFD6E3FF),
        onPrimaryContainer = Color(0xFF001B3E),
        secondary = Color(0xFF565E71),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFDAE2F9),
        onSecondaryContainer = Color(0xFF131C2B),
        tertiary = Color(0xFF705575),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFFAD8FD),
        onTertiaryContainer = Color(0xFF28132E),
        background = Color(0xFFF9F9FF),
        onBackground = Color(0xFF191C20),
        surface = Color(0xFFF9F9FF),
        onSurface = Color(0xFF191C20),
        surfaceVariant = Color(0xFFE0E2EC),
        onSurfaceVariant = Color(0xFF44474E),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF3F3FA),
        surfaceContainer = Color(0xFFEDEDF4),
        surfaceContainerHigh = Color(0xFFE7E8EE),
        surfaceContainerHighest = Color(0xFFE2E2E9),
        outline = Color(0xFF74777F),
        outlineVariant = Color(0xFFC4C6D0),
        error = AlertRed,
        onError = Color.White,
        errorContainer = AlertRedContainer,
        onErrorContainer = Color(0xFF410002)
    )

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
        onTertiary = Color.White,
        tertiaryContainer = AegeanContainer,
        onTertiaryContainer = AegeanOnContainer,
        background = SilkBackground,
        onBackground = SilkOnSurface,
        surface = SilkSurface,
        onSurface = SilkOnSurface,
        surfaceVariant = SilkSurfaceVariant,
        onSurfaceVariant = SilkMuted,
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF7F4EC),
        surfaceContainer = Color(0xFFF1EDE3),
        surfaceContainerHigh = Color(0xFFEBE6DA),
        surfaceContainerHighest = Color(0xFFE5DFD2),
        outline = SilkOutline,
        outlineVariant = Color(0xFFE2DDD1),
        error = AlertRed,
        onError = Color.White,
        errorContainer = AlertRedContainer,
        onErrorContainer = Color(0xFF410002)
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
        onTertiary = Color.White,
        tertiaryContainer = TealContainer,
        onTertiaryContainer = TealOnContainer,
        background = PearlBackground,
        onBackground = PearlOnSurface,
        surface = PearlSurface,
        onSurface = PearlOnSurface,
        surfaceVariant = PearlSurfaceVariant,
        onSurfaceVariant = PearlMuted,
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF4F7FA),
        surfaceContainer = Color(0xFFEEF2F6),
        surfaceContainerHigh = Color(0xFFE8EDF3),
        surfaceContainerHighest = Color(0xFFE1E7EE),
        outline = PearlOutline,
        outlineVariant = Color(0xFFD8E0E8),
        error = AlertRed,
        onError = Color.White,
        errorContainer = AlertRedContainer,
        onErrorContainer = Color(0xFF410002)
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
        onTertiary = Color.White,
        tertiaryContainer = OchreContainer,
        onTertiaryContainer = OchreOnContainer,
        background = SandBackground,
        onBackground = SandOnSurface,
        surface = SandSurface,
        onSurface = SandOnSurface,
        surfaceVariant = SandSurfaceVariant,
        onSurfaceVariant = SandMuted,
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF8F5EE),
        surfaceContainer = Color(0xFFF2EEE5),
        surfaceContainerHigh = Color(0xFFECE7DC),
        surfaceContainerHighest = Color(0xFFE6E0D4),
        outline = SandOutline,
        outlineVariant = Color(0xFFE0DACB),
        error = AlertRed,
        onError = Color.White,
        errorContainer = AlertRedContainer,
        onErrorContainer = Color(0xFF410002)
    )
}

private fun darkSchemeFor(palette: AppColorPalette): ColorScheme = when (palette) {
    AppColorPalette.MATERIAL_YOU -> darkColorScheme(
        primary = Color(0xFFA9C7FF),
        onPrimary = Color(0xFF07305F),
        primaryContainer = Color(0xFF254777),
        onPrimaryContainer = Color(0xFFD6E3FF),
        secondary = Color(0xFFBEC6DC),
        onSecondary = Color(0xFF283041),
        secondaryContainer = Color(0xFF3E4759),
        onSecondaryContainer = Color(0xFFDAE2F9),
        tertiary = Color(0xFFDDBCE0),
        onTertiary = Color(0xFF3F2844),
        tertiaryContainer = Color(0xFF573E5C),
        onTertiaryContainer = Color(0xFFFAD8FD),
        background = Color(0xFF111318),
        onBackground = Color(0xFFE2E2E9),
        surface = Color(0xFF111318),
        onSurface = Color(0xFFE2E2E9),
        surfaceVariant = Color(0xFF44474E),
        onSurfaceVariant = Color(0xFFC4C6D0),
        surfaceContainerLowest = Color(0xFF0C0E13),
        surfaceContainerLow = Color(0xFF191C20),
        surfaceContainer = Color(0xFF1D2024),
        surfaceContainerHigh = Color(0xFF282A2F),
        surfaceContainerHighest = Color(0xFF33353A),
        outline = Color(0xFF8E9099),
        outlineVariant = Color(0xFF44474E),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6)
    )

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
        onTertiary = Color(0xFF003547),
        tertiaryContainer = Color(0xFF173D4E),
        onTertiaryContainer = Color(0xFFD8ECF5),
        background = DarkBgEmerald,
        onBackground = Color(0xFFEAF0EE),
        surface = DarkSurfaceEmerald,
        onSurface = Color(0xFFEAF0EE),
        surfaceVariant = DarkSurfaceVarEmerald,
        onSurfaceVariant = Color(0xFFA0AEA9),
        surfaceContainerLowest = Color(0xFF0A110F),
        surfaceContainerLow = Color(0xFF151F1C),
        surfaceContainer = Color(0xFF192421),
        surfaceContainerHigh = Color(0xFF232F2B),
        surfaceContainerHighest = Color(0xFF2C3A35),
        outline = DarkOutlineEmerald,
        outlineVariant = Color(0xFF293834),
        error = Color(0xFFF28B82),
        onError = Color(0xFF601410),
        errorContainer = Color(0xFF5C1E18),
        onErrorContainer = Color(0xFFFCE8E6)
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
        onTertiary = Color(0xFF003734),
        tertiaryContainer = Color(0xFF174A46),
        onTertiaryContainer = Color(0xFFD5F0EE),
        background = DarkBgSapphire,
        onBackground = Color(0xFFE9EFF5),
        surface = DarkSurfaceSapphire,
        onSurface = Color(0xFFE9EFF5),
        surfaceVariant = DarkSurfaceVarSapphire,
        onSurfaceVariant = Color(0xFFA1B0BF),
        surfaceContainerLowest = Color(0xFF0B1016),
        surfaceContainerLow = Color(0xFF151E28),
        surfaceContainer = Color(0xFF1A242F),
        surfaceContainerHigh = Color(0xFF242F3D),
        surfaceContainerHighest = Color(0xFF2E3A49),
        outline = DarkOutlineSapphire,
        outlineVariant = Color(0xFF2B3848),
        error = Color(0xFFF28B82),
        onError = Color(0xFF601410),
        errorContainer = Color(0xFF5C1E18),
        onErrorContainer = Color(0xFFFCE8E6)
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
        onTertiary = Color(0xFF392C0C),
        tertiaryContainer = Color(0xFF4A3B1B),
        onTertiaryContainer = Color(0xFFF2E8D2),
        background = DarkBgOlive,
        onBackground = Color(0xFFECEBE4),
        surface = DarkSurfaceOlive,
        onSurface = Color(0xFFECEBE4),
        surfaceVariant = DarkSurfaceVarOlive,
        onSurfaceVariant = Color(0xFFA9A89C),
        surfaceContainerLowest = Color(0xFF0E110D),
        surfaceContainerLow = Color(0xFF1A1F19),
        surfaceContainer = Color(0xFF1E241D),
        surfaceContainerHigh = Color(0xFF282E26),
        surfaceContainerHighest = Color(0xFF323930),
        outline = DarkOutlineOlive,
        outlineVariant = Color(0xFF2F362C),
        error = Color(0xFFF28B82),
        onError = Color(0xFF601410),
        errorContainer = Color(0xFF5C1E18),
        onErrorContainer = Color(0xFFFCE8E6)
    )
}

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
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
    val context = LocalContext.current
    val colorScheme = if (palette == AppColorPalette.MATERIAL_YOU && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (isDark) darkSchemeFor(palette) else lightSchemeFor(palette)
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
