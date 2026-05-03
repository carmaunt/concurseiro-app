package br.com.mauricio.oconcurseiro.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun OConcurseiroTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    setDarkMode(darkTheme)

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFFFFAB8A),
            onPrimary = Color(0xFF3D1800),
            primaryContainer = Color(0xFF3D2218),
            onPrimaryContainer = Color(0xFFFFDBCF),
            secondary = Color(0xFFB0B0B0),
            onSecondary = Color(0xFF1A1A1A),
            secondaryContainer = Color(0xFF2A2A2A),
            onSecondaryContainer = Color(0xFFB0B0B0),
            background = Color(0xFF121212),
            onBackground = Color(0xFFE8E8E8),
            surface = Color(0xFF1E1E1E),
            onSurface = Color(0xFFE8E8E8),
            surfaceVariant = Color(0xFF252525),
            onSurfaceVariant = Color(0xFFB0B0B0),
            outline = Color(0xFF3A3A3A),
            error = Color(0xFFFF6B6B),
            onError = Color.White,
            errorContainer = Color(0xFF2E1A1A)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFFFF6A2A),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFE7DD),
            onPrimaryContainer = Color(0xFF8B2500),
            secondary = Color(0xFF6B7280),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFF3F4F6),
            onSecondaryContainer = Color(0xFF374151),
            background = Color(0xFFF6F7FB),
            onBackground = Color(0xFF111827),
            surface = Color.White,
            onSurface = Color(0xFF111827),
            surfaceVariant = Color(0xFFF3F4F6),
            onSurfaceVariant = Color(0xFF6B7280),
            outline = Color(0xFFE5E7EB),
            error = Color(0xFFEF4444),
            onError = Color.White,
            errorContainer = Color(0xFFFEE2E2)
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = HeaderBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            val navBarColor = if (darkTheme) Color(0xFF121212).toArgb() else Color(0xFFF6F7FB).toArgb()
            window.navigationBarColor = navBarColor
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
