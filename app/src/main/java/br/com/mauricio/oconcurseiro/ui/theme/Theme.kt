package br.com.mauricio.oconcurseiro.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = BrandOrange,
    onPrimary = TextOnBrand,
    primaryContainer = BrandOrangeLight,
    onPrimaryContainer = BrandOrange,
    secondary = TextSecondary,
    onSecondary = TextOnBrand,
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
    primary = BrandOrange,
    onPrimary = TextOnBrand,
    primaryContainer = BrandOrangeLight,
    onPrimaryContainer = BrandOrange,
    secondary = TextSecondary,
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
