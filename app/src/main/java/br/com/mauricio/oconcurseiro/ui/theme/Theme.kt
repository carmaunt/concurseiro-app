package br.com.mauricio.oconcurseiro.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = TextOnBrand,
    primaryContainer = BrandPrimaryLight,
    onPrimaryContainer = BrandPrimary,
    secondary = BrandAccent,
    onSecondary = TextOnBrand,
    secondaryContainer = BrandAccentLight,
    onSecondaryContainer = BrandAccent,
    background = SurfaceBackground,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = BorderDefault,
    error = ErrorBorder,
    onError = TextOnBrand,
    errorContainer = ErrorBg
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandAccent,
    onPrimary = TextOnBrand,
    primaryContainer = BrandPrimary,
    onPrimaryContainer = BrandPrimaryLight,
    secondary = BrandAccent,
    onSecondary = TextOnBrand,
    background = TextPrimary,
    onBackground = SurfaceBackground,
    surface = TextPrimary,
    onSurface = SurfaceBackground,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = BorderDefault,
    error = ErrorBorder,
    onError = TextOnBrand,
    errorContainer = ErrorBg
)

@Composable
fun OConcurseiroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
