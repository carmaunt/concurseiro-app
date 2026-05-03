package br.com.mauricio.oconcurseiro.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

private val _isDark = mutableStateOf(false)

internal fun setDarkMode(dark: Boolean) {
    _isDark.value = dark
}

private val isDark: Boolean get() = _isDark.value

val HeaderBackground = Color(0xFFFF6A2A)

val BrandPrimary: Color get() = if (isDark) Color(0xFFFFAB8A) else Color(0xFFFF6A2A)
val BrandPrimaryLight: Color get() = if (isDark) Color(0xFF3D2218) else Color(0xFFFFE7DD)
val BrandPrimaryDisabled: Color get() = if (isDark) Color(0xFF7A4A35) else Color(0xFFFFC3A8)
val BrandPrimaryBackground: Color get() = if (isDark) Color(0xFF2A1A10) else Color(0xFFFFF7F3)

val TextPrimary: Color get() = if (isDark) Color(0xFFE8E8E8) else Color(0xFF111827)
val TextSecondary: Color get() = if (isDark) Color(0xFFB0B0B0) else Color(0xFF6B7280)
val TextLabel: Color get() = if (isDark) Color(0xFFCCCCCC) else Color(0xFF374151)
val TextPlaceholder: Color get() = if (isDark) Color(0xFF707070) else Color(0xFF9CA3AF)
val TextOnBrand: Color = Color.White

val SurfaceBackground: Color get() = if (isDark) Color(0xFF121212) else Color(0xFFF6F7FB)
val SurfaceCard: Color get() = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF3F4F6)
val SurfaceWhite: Color get() = if (isDark) Color(0xFF252525) else Color.White

val BorderDefault: Color get() = if (isDark) Color(0xFF3A3A3A) else Color(0xFFE5E7EB)
val BorderCircle: Color get() = if (isDark) Color(0xFF444444) else Color(0xFFE5E7EB)

val SurfaceChip: Color get() = if (isDark) Color(0xFF2A2A2A) else Color(0xFFF7F8FA)

val SuccessBg: Color get() = if (isDark) Color(0xFF1A2E1A) else Color(0xFFDCFCE7)
val SuccessBorder: Color get() = if (isDark) Color(0xFF4ADE80) else Color(0xFF16A34A)

val ErrorBg: Color get() = if (isDark) Color(0xFF2E1A1A) else Color(0xFFFEE2E2)
val ErrorBorder: Color get() = if (isDark) Color(0xFFFF6B6B) else Color(0xFFEF4444)

val WarningBg: Color get() = if (isDark) Color(0xFF2D1F00) else Color(0xFFFEF3C7)
val WarningBar: Color get() = if (isDark) Color(0xFFFBBF24) else Color(0xFFF59E0B)
