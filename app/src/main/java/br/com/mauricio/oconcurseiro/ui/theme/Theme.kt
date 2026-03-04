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
            primary = Color(0xFF78B4E0),
            onPrimary = Color.White,
            primaryContainer = Color(0xFF2A3540),
            onPrimaryContainer = Color(0xFFD0E4F0),
            secondary = Color(0xFFA0D0BE),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFF1E302A),
            onSecondaryContainer = Color(0xFFA0D0BE),
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
            val navBarColor = if (darkTheme) Color(0xFF121212).toArgb() else Color(0xFFF5F6FA).toArgb()
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
