package br.com.mauricio.oconcurseiro.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

private val _isDark = mutableStateOf(false)

internal fun setDarkMode(dark: Boolean) {
    _isDark.value = dark
}

private val isDark: Boolean get() = _isDark.value

val AppWhite = Color(0xFFFFFFFF)
val AppBackground = Color(0xFFF7F9FC)

val LogoNavy = Color(0xFF20384F)
val LogoNavyDark = Color(0xFF172C42)
val LogoNavyDeep = Color(0xFF102235)

val LogoGreen = Color(0xFF22C55E)
val LogoGreenDark = Color(0xFF16A34A)
val LogoGreenSoft = Color(0xFFEAFBF0)

val HeaderBackground: Color get() = if (isDark) LogoNavyDeep else LogoNavy

val BrandPrimary: Color get() = if (isDark) LogoGreen else LogoNavy
val BrandPrimaryLight: Color get() = if (isDark) Color(0xFF233B52) else Color(0xFFEAF1F7)
val BrandPrimaryDisabled: Color get() = if (isDark) Color(0xFF4D6175) else Color(0xFF9AAABA)
val BrandPrimaryBackground: Color get() = if (isDark) Color(0xFF12263A) else AppWhite

val TextPrimary: Color get() = if (isDark) Color(0xFFF4F7FA) else Color(0xFF142334)
val TextSecondary: Color get() = if (isDark) Color(0xFFC2CBD3) else Color(0xFF52677A)
val TextLabel: Color get() = if (isDark) Color(0xFFD7DEE5) else LogoNavy
val TextPlaceholder: Color get() = if (isDark) Color(0xFF7F909F) else Color(0xFF8797A6)
val TextOnBrand: Color get() = if (isDark) LogoNavyDeep else AppWhite

val SurfaceBackground: Color get() = if (isDark) Color(0xFF0F1F30) else AppBackground
val SurfaceCard: Color get() = if (isDark) LogoNavyDark else AppWhite
val SurfaceWhite: Color get() = if (isDark) Color(0xFF14283D) else AppWhite

val BorderDefault: Color get() = if (isDark) Color(0xFF2F4A63) else Color(0xFFE5EAF0)
val BorderCircle: Color get() = if (isDark) Color(0xFF3A536A) else Color(0xFFDDE5ED)

val SurfaceChip: Color get() = if (isDark) Color(0xFF1C344B) else Color(0xFFF1F5F9)

val SuccessBg: Color get() = if (isDark) Color(0xFF123524) else LogoGreenSoft
val SuccessBorder: Color get() = if (isDark) Color(0xFF4ADE80) else LogoGreenDark

val ErrorBg: Color get() = if (isDark) Color(0xFF3A1F24) else Color(0xFFFFE4E6)
val ErrorBorder: Color get() = if (isDark) Color(0xFFFF8A96) else Color(0xFFDC3545)

val WarningBg: Color get() = if (isDark) Color(0xFF3A2F16) else Color(0xFFFFF7E6)
val WarningBar: Color get() = if (isDark) Color(0xFFE6C15A) else Color(0xFFF59E0B)