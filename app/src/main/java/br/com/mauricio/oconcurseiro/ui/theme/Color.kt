package br.com.mauricio.oconcurseiro.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

private val _isDark = mutableStateOf(false)

internal fun setDarkMode(dark: Boolean) {
    _isDark.value = dark
}

private val isDark: Boolean get() = _isDark.value

val HeaderBackground = Color(0xFF2D3E50)

val BrandPrimary: Color get() = if (isDark) Color(0xFF78B4E0) else Color(0xFF2D3E50)
val BrandPrimaryLight: Color get() = if (isDark) Color(0xFF2A3540) else Color(0xFFB8CDE0)
val BrandPrimaryDisabled: Color get() = if (isDark) Color(0xFF5A7A90) else Color(0xFF8BA0B5)
val BrandPrimaryBackground: Color get() = if (isDark) Color(0xFF1E2428) else Color(0xFFF0F3F7)

val BrandCream: Color get() = if (isDark) Color(0xFF3A3530) else Color(0xFFF2E6D0)
val BrandAccent: Color get() = if (isDark) Color(0xFFA0D0BE) else Color(0xFF7D9B91)
val BrandAccentLight: Color get() = if (isDark) Color(0xFF1E302A) else Color(0xFFE5EDEA)

val TextPrimary: Color get() = if (isDark) Color(0xFFE8E8E8) else Color(0xFF1A2A3A)
val TextSecondary: Color get() = if (isDark) Color(0xFFB0B0B0) else Color(0xFF5A6B7D)
val TextLabel: Color get() = if (isDark) Color(0xFFCCCCCC) else Color(0xFF374151)
val TextPlaceholder: Color get() = if (isDark) Color(0xFF707070) else Color(0xFF9CA3AF)
val TextOnBrand: Color = Color.White

val SurfaceBackground: Color get() = if (isDark) Color(0xFF121212) else Color(0xFFF5F6FA)
val SurfaceCard: Color get() = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF0F1F5)
val SurfaceWhite: Color get() = if (isDark) Color(0xFF252525) else Color.White

val BorderDefault: Color get() = if (isDark) Color(0xFF3A3A3A) else Color(0xFFDDE1E8)
val BorderCircle: Color get() = if (isDark) Color(0xFF444444) else Color(0xFFDDE1E8)

val SurfaceChip: Color get() = if (isDark) Color(0xFF2A2A2A) else Color(0xFFF7F8FA)

val SuccessBg: Color get() = if (isDark) Color(0xFF1A2E1A) else Color(0xFFDCFCE7)
val SuccessBorder: Color get() = if (isDark) Color(0xFF4ADE80) else Color(0xFF16A34A)

val ErrorBg: Color get() = if (isDark) Color(0xFF2E1A1A) else Color(0xFFFEE2E2)
val ErrorBorder: Color get() = if (isDark) Color(0xFFFF6B6B) else Color(0xFFEF4444)
