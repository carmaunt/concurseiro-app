package br.com.mauricio.oconcurseiro.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
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
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    setDarkMode(darkTheme)

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFF5B8AB5),
            onPrimary = Color.White,
            primaryContainer = Color(0xFF1C2B38),
            onPrimaryContainer = Color(0xFFB0C8DC),
            secondary = Color(0xFF8FB5A8),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFF1A2B25),
            onSecondaryContainer = Color(0xFF8FB5A8),
            background = Color(0xFF0F1419),
            onBackground = Color(0xFFE4E8EC),
            surface = Color(0xFF151D28),
            onSurface = Color(0xFFE4E8EC),
            surfaceVariant = Color(0xFF1A2230),
            onSurfaceVariant = Color(0xFF9BAABC),
            outline = Color(0xFF283848),
            error = Color(0xFFEF4444),
            onError = Color.White,
            errorContainer = Color(0xFF2D1215)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF2D3E50),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFDFE5EB),
            onPrimaryContainer = Color(0xFF2D3E50),
            secondary = Color(0xFF7D9B91),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFE5EDEA),
            onSecondaryContainer = Color(0xFF7D9B91),
            background = Color(0xFFF5F6FA),
            onBackground = Color(0xFF1A2A3A),
            surface = Color.White,
            onSurface = Color(0xFF1A2A3A),
            surfaceVariant = Color(0xFFF0F1F5),
            onSurfaceVariant = Color(0xFF5A6B7D),
            outline = Color(0xFFDDE1E8),
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
            val navBarColor = if (darkTheme) Color(0xFF0F1419).toArgb() else Color(0xFFF5F6FA).toArgb()
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
