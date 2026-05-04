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
            primary = LogoGreen,
            onPrimary = LogoNavyDeep,
            primaryContainer = LogoNavyDark,
            onPrimaryContainer = Color(0xFFEAFBF0),
            secondary = Color(0xFFC2CBD3),
            onSecondary = LogoNavyDeep,
            secondaryContainer = Color(0xFF1C344B),
            onSecondaryContainer = Color(0xFFD7DEE5),
            background = Color(0xFF0F1F30),
            onBackground = Color(0xFFF4F7FA),
            surface = Color(0xFF14283D),
            onSurface = Color(0xFFF4F7FA),
            surfaceVariant = LogoNavyDark,
            onSurfaceVariant = Color(0xFFC2CBD3),
            outline = Color(0xFF2F4A63),
            error = Color(0xFFFF8A96),
            onError = AppWhite,
            errorContainer = Color(0xFF3A1F24)
        )
    } else {
        lightColorScheme(
            primary = LogoNavy,
            onPrimary = AppWhite,
            primaryContainer = Color(0xFFEAF1F7),
            onPrimaryContainer = LogoNavyDeep,
            secondary = LogoGreen,
            onSecondary = AppWhite,
            secondaryContainer = LogoGreenSoft,
            onSecondaryContainer = LogoGreenDark,
            background = AppBackground,
            onBackground = Color(0xFF142334),
            surface = AppWhite,
            onSurface = Color(0xFF142334),
            surfaceVariant = Color(0xFFF1F5F9),
            onSurfaceVariant = Color(0xFF52677A),
            outline = Color(0xFFE5EAF0),
            error = Color(0xFFDC3545),
            onError = AppWhite,
            errorContainer = Color(0xFFFFE4E6)
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            window.statusBarColor = HeaderBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false

            val navBarColor = if (darkTheme) {
                Color(0xFF0F1F30).toArgb()
            } else {
                AppBackground.toArgb()
            }

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