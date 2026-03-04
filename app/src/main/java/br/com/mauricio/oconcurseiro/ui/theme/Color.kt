package br.com.mauricio.oconcurseiro.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

private val _isDark = mutableStateOf(false)

internal fun setDarkMode(dark: Boolean) {
    _isDark.value = dark
}

private val isDark: Boolean get() = _isDark.value

val HeaderBackground = Color(0xFF2D3E50)

val BrandPrimary: Color get() = if (isDark) Color(0xFF6EA8D4) else Color(0xFF2D3E50)
val BrandPrimaryLight: Color get() = if (isDark) Color(0xFF253A50) else Color(0xFFDFE5EB)
val BrandPrimaryDisabled: Color get() = if (isDark) Color(0xFF4A6478) else Color(0xFF8BA0B5)
val BrandPrimaryBackground: Color get() = if (isDark) Color(0xFF172230) else Color(0xFFF0F3F7)

val BrandCream: Color get() = if (isDark) Color(0xFF2E2822) else Color(0xFFF2E6D0)
val BrandAccent: Color get() = if (isDark) Color(0xFF9ECAB8) else Color(0xFF7D9B91)
val BrandAccentLight: Color get() = if (isDark) Color(0xFF1E3530) else Color(0xFFE5EDEA)

val TextPrimary: Color get() = if (isDark) Color(0xFFECF0F4) else Color(0xFF1A2A3A)
val TextSecondary: Color get() = if (isDark) Color(0xFFAABBCC) else Color(0xFF5A6B7D)
val TextLabel: Color get() = if (isDark) Color(0xFFC2CED8) else Color(0xFF374151)
val TextPlaceholder: Color get() = if (isDark) Color(0xFF6B7D90) else Color(0xFF9CA3AF)
val TextOnBrand: Color = Color.White

val SurfaceBackground: Color get() = if (isDark) Color(0xFF101820) else Color(0xFFF5F6FA)
val SurfaceCard: Color get() = if (isDark) Color(0xFF1E2A38) else Color(0xFFF0F1F5)
val SurfaceWhite: Color get() = if (isDark) Color(0xFF1A2535) else Color.White

val BorderDefault: Color get() = if (isDark) Color(0xFF334858) else Color(0xFFDDE1E8)
val BorderCircle: Color get() = if (isDark) Color(0xFF3D5265) else Color(0xFFDDE1E8)

val SurfaceChip: Color get() = if (isDark) Color(0xFF243040) else Color(0xFFF7F8FA)

val SuccessBg: Color get() = if (isDark) Color(0xFF142E1E) else Color(0xFFDCFCE7)
val SuccessBorder: Color get() = if (isDark) Color(0xFF34D66E) else Color(0xFF16A34A)

val ErrorBg: Color get() = if (isDark) Color(0xFF351820) else Color(0xFFFEE2E2)
val ErrorBorder: Color get() = if (isDark) Color(0xFFFF6B6B) else Color(0xFFEF4444)
