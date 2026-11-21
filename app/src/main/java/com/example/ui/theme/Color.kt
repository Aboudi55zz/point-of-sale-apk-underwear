package com.example.ui.theme

import androidx.compose.ui.graphics.Color

enum class AppColorPalette(
    val titleAr: String,
    val subtitleAr: String,
    val previewPrimary: Color,
    val previewSecondary: Color,
    val previewBackground: Color
) {
    EMERALD_GOLD(
        titleAr = "زمرد وذهب دمشقي",
        subtitleAr = "أخضر زمردي عميق مع لمسات ذهبية دافئة وعاج ناعم",
        previewPrimary = Color(0xFF08514A),
        previewSecondary = Color(0xFFC88A2E),
        previewBackground = Color(0xFFF5F2EA)
    ),
    SAPPHIRE_CORAL(
        titleAr = "ياقوت ومرجان",
        subtitleAr = "أزرق نيلي متوازن مع برتقالي مرجاني ناعم وورق لؤلؤي",
        previewPrimary = Color(0xFF1B4965),
        previewSecondary = Color(0xFFD1603D),
        previewBackground = Color(0xFFF3F6F9)
    ),
    OLIVE_COPPER(
        titleAr = "زيتوني ونحاس",
        subtitleAr = "أخضر زيتوني دافئ مع نحاس معتّق وبيج رملي مريح",
        previewPrimary = Color(0xFF3E5641),
        previewSecondary = Color(0xFFB86B35),
        previewBackground = Color(0xFFF6F3EC)
    )
}

enum class ThemeMode(val titleAr: String) {
    SYSTEM("تلقائي (حسب الجهاز)"),
    LIGHT("فاتح"),
    DARK("داكن")
}

// Semantic Status Colors (harmonized)
val AlertRed = Color(0xFFC0392B)
val AlertRedContainer = Color(0xFFFBE6E4)
val SuccessGreen = Color(0xFF1B7A4B)
val SuccessGreenContainer = Color(0xFFDCF4E7)
val WarningAmber = Color(0xFFC97312)
val WarningAmberContainer = Color(0xFFFEF0D6)

// --- Palette 1: Emerald & Warm Gold ---
val EmeraldPrimary = Color(0xFF08514A)
val EmeraldOnPrimary = Color(0xFFFFFFFF)
val EmeraldContainer = Color(0xFFD4EFE9)
val EmeraldOnContainer = Color(0xFF04302B)

val GoldSecondary = Color(0xFFB87B1E)
val GoldOnSecondary = Color(0xFFFFFFFF)
val GoldContainer = Color(0xFFFBE9C8)
val GoldOnContainer = Color(0xFF3D2500)

val AegeanTertiary = Color(0xFF265C73)
val AegeanContainer = Color(0xFFD8ECF5)
val AegeanOnContainer = Color(0xFF0D2B38)

val SilkBackground = Color(0xFFF5F2EA)
val SilkSurface = Color(0xFFFCFBF7)
val SilkSurfaceVariant = Color(0xFFEAE4D7)
val SilkOnSurface = Color(0xFF18201E)
val SilkMuted = Color(0xFF5A6562)
val SilkOutline = Color(0xFFD8D1C2)

// --- Palette 2: Sapphire & Warm Coral ---
val SapphirePrimary = Color(0xFF1B4965)
val SapphireOnPrimary = Color(0xFFFFFFFF)
val SapphireContainer = Color(0xFFD6E9F5)
val SapphireOnContainer = Color(0xFF0B2536)

val CoralSecondary = Color(0xFFC75B39)
val CoralOnSecondary = Color(0xFFFFFFFF)
val CoralContainer = Color(0xFFFCE3DA)
val CoralOnContainer = Color(0xFF47180A)

val TealTertiary = Color(0xFF2A7B76)
val TealContainer = Color(0xFFD5F0EE)
val TealOnContainer = Color(0xFF0B3431)

val PearlBackground = Color(0xFFF3F6F9)
val PearlSurface = Color(0xFFFBFCFE)
val PearlSurfaceVariant = Color(0xFFE4EBF1)
val PearlOnSurface = Color(0xFF16202A)
val PearlMuted = Color(0xFF566471)
val PearlOutline = Color(0xFFCED8E2)

// --- Palette 3: Olive & Burnished Copper ---
val OlivePrimary = Color(0xFF3E5641)
val OliveOnPrimary = Color(0xFFFFFFFF)
val OliveContainer = Color(0xFFDCE8DC)
val OliveOnContainer = Color(0xFF1A291C)

val CopperSecondary = Color(0xFFB86B35)
val CopperOnSecondary = Color(0xFFFFFFFF)
val CopperContainer = Color(0xFFF9E4D4)
val CopperOnContainer = Color(0xFF421E06)

val OchreTertiary = Color(0xFF7B6332)
val OchreContainer = Color(0xFFF2E8D2)
val OchreOnContainer = Color(0xFF33260C)

val SandBackground = Color(0xFFF6F3EC)
val SandSurface = Color(0xFFFDFBF7)
val SandSurfaceVariant = Color(0xFFECE6D9)
val SandOnSurface = Color(0xFF21211B)
val SandMuted = Color(0xFF636156)
val SandOutline = Color(0xFFD9D2C3)

// Dark Surface Foundation
val DarkBgEmerald = Color(0xFF0E1614)
val DarkSurfaceEmerald = Color(0xFF16211E)
val DarkSurfaceVarEmerald = Color(0xFF21302C)
val DarkOutlineEmerald = Color(0xFF33443F)

val DarkBgSapphire = Color(0xFF0F151C)
val DarkSurfaceSapphire = Color(0xFF17202B)
val DarkSurfaceVarSapphire = Color(0xFF222E3D)
val DarkOutlineSapphire = Color(0xFF334254)

val DarkBgOlive = Color(0xFF131612)
val DarkSurfaceOlive = Color(0xFF1C211B)
val DarkSurfaceVarOlive = Color(0xFF272E25)
val DarkOutlineOlive = Color(0xFF3B4338)
