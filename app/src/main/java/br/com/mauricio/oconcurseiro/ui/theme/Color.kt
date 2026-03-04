package br.com.mauricio.oconcurseiro.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

private val _isDark = mutableStateOf(false)

internal fun setDarkMode(dark: Boolean) {
    _isDark.value = dark
}

private val isDark: Boolean get() = _isDark.value

val HeaderBackground = Color(0xFF2D3E50)

val BrandPrimary: Color get() = if (isDark) Color(0xFF5B8AB5) else Color(0xFF2D3E50)
val BrandPrimaryLight: Color get() = if (isDark) Color(0xFF1C2B38) else Color(0xFFDFE5EB)
val BrandPrimaryDisabled: Color get() = if (isDark) Color(0xFF3A5060) else Color(0xFF8BA0B5)
val BrandPrimaryBackground: Color get() = if (isDark) Color(0xFF131C25) else Color(0xFFF0F3F7)

val BrandCream: Color get() = if (isDark) Color(0xFF2A2520) else Color(0xFFF2E6D0)
val BrandAccent: Color get() = if (isDark) Color(0xFF8FB5A8) else Color(0xFF7D9B91)
val BrandAccentLight: Color get() = if (isDark) Color(0xFF1A2B25) else Color(0xFFE5EDEA)

val TextPrimary: Color get() = if (isDark) Color(0xFFE4E8EC) else Color(0xFF1A2A3A)
val TextSecondary: Color get() = if (isDark) Color(0xFF9BAABC) else Color(0xFF5A6B7D)
val TextLabel: Color get() = if (isDark) Color(0xFFB0BCC8) else Color(0xFF374151)
val TextPlaceholder: Color get() = if (isDark) Color(0xFF536475) else Color(0xFF9CA3AF)
val TextOnBrand: Color = Color.White

val SurfaceBackground: Color get() = if (isDark) Color(0xFF0F1419) else Color(0xFFF5F6FA)
val SurfaceCard: Color get() = if (isDark) Color(0xFF1A2230) else Color(0xFFF0F1F5)
val SurfaceWhite: Color get() = if (isDark) Color(0xFF151D28) else Color.White

val BorderDefault: Color get() = if (isDark) Color(0xFF283848) else Color(0xFFDDE1E8)
val BorderCircle: Color get() = if (isDark) Color(0xFF283848) else Color(0xFFDDE1E8)

val SurfaceChip: Color get() = if (isDark) Color(0xFF1A2230) else Color(0xFFF7F8FA)

val SuccessBg: Color get() = if (isDark) Color(0xFF0D2618) else Color(0xFFDCFCE7)
val SuccessBorder: Color get() = if (isDark) Color(0xFF22C55E) else Color(0xFF16A34A)

val ErrorBg: Color get() = if (isDark) Color(0xFF2D1215) else Color(0xFFFEE2E2)
val ErrorBorder: Color get() = if (isDark) Color(0xFFEF4444) else Color(0xFFEF4444)
